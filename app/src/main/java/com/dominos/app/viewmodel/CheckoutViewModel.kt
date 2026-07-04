package com.dominos.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dominos.app.data.model.*
import com.dominos.app.data.repository.DominosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class OrderUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val orderResponse: OrderResponse? = null,
    val validatedOrder: OrderResponse? = null,
    val pricedOrder: OrderResponse? = null,
    val placedOrder: OrderResponse? = null,
    val serviceMethod: String = "Carryout",
    val estimatedWait: String? = null,
    val orderPlacedSuccessfully: Boolean = false,
    val pulseOrderGuid: String? = null,
    val orderId: String? = null,
    val tipAmount: Double = 0.0
)

class CheckoutViewModel : ViewModel() {
    private val repository = DominosRepository()
    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState: StateFlow<OrderUiState> = _uiState

    fun setServiceMethod(method: String) { _uiState.value = _uiState.value.copy(serviceMethod = method) }
    fun setTipAmount(amount: Double) { _uiState.value = _uiState.value.copy(tipAmount = amount) }

    private fun buildOrder(storeId: String, customer: SavedCustomer, products: List<CartItem>, serviceMethod: String, tipAmount: Double = 0.0): OrderPayload {
        val orderProducts = products.mapIndexed { index, item ->
            OrderProduct(
                code = item.productCode,
                qty = item.quantity,
                id = index + 1,
                isNew = true,
                options = item.options
            )
        }

        val address = if (serviceMethod == "Delivery") {
            val addrLines = customer.street.split(" ", limit = 2)
            OrderAddress(
                street = customer.street,
                streetNumber = addrLines.getOrNull(0) ?: "",
                streetName = addrLines.getOrNull(1) ?: customer.street,
                city = customer.city,
                region = customer.region,
                postalCode = customer.postalCode,
                type = "House"
            )
        } else null

        val orderTotal = products.sumOf { it.quantity * (it.price?.toDoubleOrNull() ?: 0.0) }
        return OrderPayload(
            storeID = storeId,
            firstName = customer.firstName.ifBlank { "Guest" },
            lastName = customer.lastName,
            phone = customer.phone.ifBlank { "555-0100" },
            email = customer.email.ifBlank { "guest@order.com" },
            address = address,
            products = orderProducts,
            serviceMethod = serviceMethod,
            languageCode = "en",
            orderChannel = "OLO",
            orderMethod = "Web",
            sourceOrganizationURI = "order.dominos.com",
            noCombine = true,
            version = "1.0",
            payments = listOf(PaymentPayload(amount = "%.2f".format(orderTotal), tipAmount = "%.2f".format(tipAmount)))
        )
    }

    fun placeOrder(storeId: String, customer: SavedCustomer, cartItems: List<CartItem>, serviceMethod: String, tipAmount: Double = 0.0) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val order = buildOrder(storeId, customer, cartItems, serviceMethod, tipAmount)

            val validateResult = repository.validateOrder(order)
            validateResult.fold(
                onSuccess = { validated ->
                    _uiState.value = _uiState.value.copy(validatedOrder = validated)
                    val priceResult = repository.priceOrder(order)
                    priceResult.fold(
                        onSuccess = { priced ->
                            _uiState.value = _uiState.value.copy(pricedOrder = priced, estimatedWait = priced.estimatedWaitMinutes)
                            val placeResult = repository.placeOrder(order)
                            placeResult.fold(
                                onSuccess = { placed ->
                                    _uiState.value = _uiState.value.copy(
                                        isLoading = false, placedOrder = placed, orderResponse = placed,
                                        orderPlacedSuccessfully = true,
                                        pulseOrderGuid = placed.pulseOrderGuid ?: placed.order?.pulseOrderGuid,
                                        orderId = placed.order?.orderID ?: placed.orderID
                                    )
                                },
                                onFailure = { e -> _uiState.value = _uiState.value.copy(isLoading = false, error = "Place order failed: ${e.message}") }
                            )
                        },
                        onFailure = { e -> _uiState.value = _uiState.value.copy(isLoading = false, error = "Price failed: ${e.message}") }
                    )
                },
                onFailure = { e -> _uiState.value = _uiState.value.copy(isLoading = false, error = "Validation failed: ${e.message}") }
            )
        }
    }

    fun reset() { _uiState.value = OrderUiState() }
}
