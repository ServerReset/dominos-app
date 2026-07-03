package com.dominos.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.dominos.app.data.local.LocalStorage
import com.dominos.app.data.model.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val storeId: String? = null,
    val itemCounter: Int = 0
)

class CartViewModel(application: Application) : AndroidViewModel(application) {
    private val storage = LocalStorage(application)
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState

    init {
        val savedItems = storage.getCartItems()
        val savedStoreId = storage.getCartStoreId()
        if (savedItems.isNotEmpty()) {
            val nextId = (savedItems.maxOfOrNull { it.id } ?: 0) + 1
            _uiState.value = CartUiState(items = savedItems, storeId = savedStoreId, itemCounter = nextId)
        }
    }

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
            _uiState.value = current.copy(items = current.items + newItem, storeId = storeId, itemCounter = current.itemCounter + 1)
        }
        persistCart()
    }

    fun updateQuantity(itemId: Int, delta: Int) {
        val current = _uiState.value
        _uiState.value = current.copy(items = current.items.mapNotNull { item ->
            if (item.id == itemId) {
                val newQty = item.quantity + delta
                if (newQty <= 0) null else item.copy(quantity = newQty)
            } else item
        })
        persistCart()
    }

    fun removeItem(itemId: Int) {
        _uiState.value = _uiState.value.copy(items = _uiState.value.items.filter { it.id != itemId })
        persistCart()
    }

    fun clearCart() { _uiState.value = CartUiState(); persistCart() }

    fun getSubtotal(): Double = _uiState.value.items.fold(0.0) { acc, item ->
        acc + ((item.price?.toDoubleOrNull() ?: 0.0) * item.quantity)
    }

    fun getItemCount(): Int = _uiState.value.items.sumOf { it.quantity }

    private fun persistCart() {
        val state = _uiState.value
        storage.saveCart(state.items, state.storeId)
    }
}
