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
    val serviceMethod: String = "Delivery",
    val estimatedWait: String? = null,
    val orderPlacedSuccessfully: Boolean = false,
    val pulseOrderGuid: String? = null
)

class CheckoutViewModel : ViewModel() {
    private val repository = DominosRepository()
    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState: StateFlow<OrderUiState> = _uiState

    fun setServiceMethod(method: String) {
        _uiState.value = _uiState.value.copy(serviceMethod = method)
    }

    private fun buildOrder(
        storeId: String,
        customer: SavedCustomer,
        products: List<CartItem>,
        serviceMethod: String
    ): OrderPayload {
        val orderProducts = products.mapIndexed { index, item ->
            OrderProduct(
                code = item.productCode,
                qty = item.quantity,
                id = index + 1,
                isNew = true,
                options = item.options
            )
        }

        val addrLines = customer.street.split(" ", limit = 2)
        val streetNumber = addrLines.getOrNull(0) ?: ""
        val streetName = addrLines.getOrNull(1) ?: customer.street

        val address = OrderAddress(
            street = customer.street,
            streetNumber = streetNumber,
            streetName = streetName,
            city = customer.city,
            region = customer.region,
            postalCode = customer.postalCode,
            type = "House"
        )

        return OrderPayload(
            storeID = storeId,
            firstName = customer.firstName,
            lastName = customer.lastName,
            phone = customer.phone,
            email = customer.email,
            address = address,
            products = orderProducts,
            serviceMethod = serviceMethod,
            languageCode = "en",
            orderChannel = "OLO",
            orderMethod = "Web",
            sourceOrganizationURI = "order.dominos.com",
            noCombine = true,
            version = "1.0"
        )
    }

    fun placeOrder(
        storeId: String,
        customer: SavedCustomer,
        cartItems: List<CartItem>,
        serviceMethod: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val order = buildOrder(storeId, customer, cartItems, serviceMethod)

            val validateResult = repository.validateOrder(order)
            validateResult.fold(
                onSuccess = { validated ->
                    _uiState.value = _uiState.value.copy(validatedOrder = validated)

                    val priceResult = repository.priceOrder(order)
                    priceResult.fold(
                        onSuccess = { priced ->
                            _uiState.value = _uiState.value.copy(
                                pricedOrder = priced,
                                estimatedWait = priced.estimatedWaitMinutes
                            )

                            val placeResult = repository.placeOrder(order)
                            placeResult.fold(
                                onSuccess = { placed ->
                                    _uiState.value = _uiState.value.copy(
                                        isLoading = false,
                                        placedOrder = placed,
                                        orderResponse = placed,
                                        orderPlacedSuccessfully = true,
                                        pulseOrderGuid = placed.pulseOrderGuid ?: placed.order?.pulseOrderGuid
                                    )
                                },
                                onFailure = { e ->
                                    _uiState.value = _uiState.value.copy(
                                        isLoading = false,
                                        error = "Place order failed: ${e.message}"
                                    )
                                }
                            )
                        },
                        onFailure = { e ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = "Price failed: ${e.message}"
                            )
                        }
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Validation failed: ${e.message}"
                    )
                }
            )
        }
    }

    fun reset() {
        _uiState.value = OrderUiState()
    }
}
