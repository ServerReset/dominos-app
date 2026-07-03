package com.dominos.app.data.model

import org.junit.Assert.*
import org.junit.Test

class ModelTest {
    @Test fun `CartItem defaults`() {
        val item = CartItem(productCode = "P12IPAZA", productName = "Pepperoni Pizza", quantity = 1, price = "12.99", id = 1)
        assertEquals("P12IPAZA", item.productCode); assertEquals(1, item.quantity); assertNull(item.options)
    }
    @Test fun `CartItem with options`() {
        val opts = mapOf("X" to mapOf("1/1" to "1"))
        val item = CartItem(productCode = "P12IPAZA", productName = "Pepperoni Pizza", quantity = 2, price = "25.98", options = opts, sizeCode = "Large", flavorCode = "HandTossed", id = 1)
        assertEquals(opts, item.options); assertEquals("Large", item.sizeCode)
    }
    @Test fun `SavedCustomer defaults`() { val c = SavedCustomer(); assertEquals("", c.firstName); assertEquals("", c.email); assertFalse(c.isLoggedIn) }
    @Test fun `SavedCustomer with values`() {
        val c = SavedCustomer(firstName = "John", lastName = "Doe", email = "john@example.com", phone = "555-0100", street = "123 Main St", city = "Seaside", region = "CA", postalCode = "93955", isLoggedIn = true)
        assertEquals("John", c.firstName); assertEquals("CA", c.region); assertEquals("93955", c.postalCode); assertTrue(c.isLoggedIn)
    }
    @Test fun `OrderPayload builds correctly`() {
        val product = OrderProduct(code = "P12IPAZA", qty = 1, id = 1, isNew = true)
        val address = OrderAddress(street = "123 Main St", streetNumber = "123", streetName = "Main St", city = "Seaside", region = "CA", postalCode = "93955", type = "House")
        val order = OrderPayload(storeID = "1234", firstName = "John", lastName = "Doe", phone = "555-0100", email = "john@example.com", address = address, products = listOf(product), serviceMethod = "Delivery")
        assertEquals("1234", order.storeID); assertEquals("Delivery", order.serviceMethod); assertEquals(1, order.products?.size)
    }
}
