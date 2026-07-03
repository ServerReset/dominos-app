package com.dominos.app.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.dominos.app.data.model.CartItem
import com.dominos.app.ui.theme.DominosTheme
import org.junit.Rule
import org.junit.Test

class CartScreenTest {
    @get:Rule val composeTestRule = createComposeRule()
    @Test fun emptyState() {
        composeTestRule.setContent { DominosTheme { CartScreen(items = emptyList(), onUpdateQuantity = { _, _ -> }, onRemoveItem = {}, onCheckout = {}, onContinueShopping = {}, onBack = {}) } }
        composeTestRule.onNodeWithText("Your cart is empty").assertIsDisplayed(); composeTestRule.onNodeWithText("Browse Menu").assertIsDisplayed()
    }
    @Test fun showsItems() {
        composeTestRule.setContent { DominosTheme { CartScreen(items = listOf(CartItem(productCode = "P12IPAZA", productName = "Pepperoni Pizza", quantity = 1, price = "12.99", id = 1)), onUpdateQuantity = { _, _ -> }, onRemoveItem = {}, onCheckout = {}, onContinueShopping = {}, onBack = {}) } }
        composeTestRule.onNodeWithText("Pepperoni Pizza").assertIsDisplayed(); composeTestRule.onNodeWithText("Checkout").assertIsDisplayed()
    }
}
