package com.dominos.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.dominos.app.data.local.LocalStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class OrderHistoryUiState(
    val orders: List<OrderHistoryEntry> = emptyList()
)

data class OrderHistoryEntry(
    val orderId: String,
    val storeId: String,
    val total: String,
    val itemsSummary: String,
    val timestamp: Long = System.currentTimeMillis()
)

class OrderHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val storage = LocalStorage(application)
    private val _uiState = MutableStateFlow(OrderHistoryUiState())
    val uiState: StateFlow<OrderHistoryUiState> = _uiState

    init { loadOrders() }

    private fun loadOrders() {
        val entries = storage.getOrderHistory().mapNotNull { parseEntry(it) }.sortedByDescending { it.timestamp }
        _uiState.value = OrderHistoryUiState(orders = entries)
    }

    fun addOrder(orderId: String, storeId: String, total: String, itemsSummary: String) {
        val entry = "$orderId|$storeId|$total|$itemsSummary|${System.currentTimeMillis()}"
        storage.addOrderHistoryEntry(entry)
        loadOrders()
    }

    private fun parseEntry(raw: String): OrderHistoryEntry? {
        val parts = raw.split("|")
        if (parts.size < 4) return null
        return OrderHistoryEntry(
            orderId = parts[0], storeId = parts[1],
            total = parts[2], itemsSummary = parts[3],
            timestamp = parts.getOrNull(4)?.toLongOrNull() ?: 0L
        )
    }
}
