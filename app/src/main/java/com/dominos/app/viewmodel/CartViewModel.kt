package com.dominos.app.viewmodel

import androidx.lifecycle.ViewModel
import com.dominos.app.data.model.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val storeId: String? = null,
    val itemCounter: Int = 0
)

class CartViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState

    fun addItem(item: CartItem, storeId: String) {
        val current = _uiState.value
        val existingIndex = current.items.indexOfFirst { it.productCode == item.productCode && it.options == item.options }
        if (existingIndex >= 0 && current.storeId == storeId) {
            val updatedList = current.items.toMutableList()
            val existing = updatedList[existingIndex]
            updatedList[existingIndex] = existing.copy(quantity = existing.quantity + item.quantity)
            _uiState.value = current.copy(items = updatedList, storeId = storeId)
        } else {
            val newItem = item.copy(id = current.itemCounter + 1)
            _uiState.value = current.copy(
                items = current.items + newItem,
                storeId = storeId,
                itemCounter = current.itemCounter + 1
            )
        }
    }

    fun updateQuantity(itemId: Int, delta: Int) {
        val current = _uiState.value
        val updatedList = current.items.map { item ->
            if (item.id == itemId) {
                val newQty = item.quantity + delta
                if (newQty <= 0) return@map null
                item.copy(quantity = newQty)
            } else item
        }.filterNotNull()
        _uiState.value = current.copy(items = updatedList)
    }

    fun removeItem(itemId: Int) {
        val current = _uiState.value
        _uiState.value = current.copy(items = current.items.filter { it.id != itemId })
    }

    fun clearCart() {
        _uiState.value = CartUiState()
    }

    fun getSubtotal(): Double {
        return _uiState.value.items.fold(0.0) { acc, item ->
            val price = item.price?.toDoubleOrNull() ?: 0.0
            acc + (price * item.quantity)
        }
    }

    fun getItemCount(): Int = _uiState.value.items.sumOf { it.quantity }
}
