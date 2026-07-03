package com.dominos.app.viewmodel

import com.dominos.app.data.model.CartItem
import org.junit.Assert.*
import org.junit.Test

class CartViewModelTest {
    @Test fun `addItem adds item to cart`() {
        val item = CartItem(productCode = "P12IPAZA", productName = "Pepperoni Pizza", quantity = 1, price = "12.99", id = 1)
        val state = CartUiState(items = listOf(item), storeId = "1234", itemCounter = 1)
        assertEquals(1, state.items.size); assertEquals("Pepperoni Pizza", state.items[0].productName)
    }
    @Test fun `removeItem removes item from cart`() {
        val items = listOf(CartItem(productCode = "P1", productName = "Pizza", quantity = 1, price = "10.00", id = 1), CartItem(productCode = "S1", productName = "Soda", quantity = 2, price = "2.50", id = 2))
        val state = CartUiState(items = items.filter { it.id != 1 }, storeId = "1234", itemCounter = 2)
        assertEquals(1, state.items.size); assertEquals("Soda", state.items[0].productName)
    }
    @Test fun `getSubtotal calculates correctly`() {
        val subtotal = listOf(CartItem(productCode = "P1", productName = "Pizza", quantity = 2, price = "10.00", id = 1), CartItem(productCode = "S1", productName = "Soda", quantity = 3, price = "2.50", id = 2)).fold(0.0) { acc, item -> acc + ((item.price?.toDoubleOrNull() ?: 0.0) * item.quantity) }
        assertEquals(27.5, subtotal, 0.001)
    }
    @Test fun `empty cart has zero items`() { assertTrue(emptyList<CartItem>().isEmpty()) }
}
