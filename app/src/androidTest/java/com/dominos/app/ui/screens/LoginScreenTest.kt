package com.dominos.app.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import com.dominos.app.ui.theme.DominosTheme
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {
    @get:Rule val composeTestRule = createComposeRule()
    @Test fun displaysTitle() {
        composeTestRule.setContent { DominosTheme { LoginScreen(onGuestContinue = {}, onLogin = { _, _ -> }, isLoading = false) } }
        composeTestRule.onNodeWithText("Domino's").assertIsDisplayed(); composeTestRule.onNodeWithText("Sign In").assertIsDisplayed()
    }
    @Test fun guestButtonDisplayed() {
        composeTestRule.setContent { DominosTheme { LoginScreen(onGuestContinue = {}, onLogin = { _, _ -> }, isLoading = false) } }
        composeTestRule.onNodeWithText("Continue as Guest").assertIsDisplayed()
    }
    @Test fun signInButtonEnabled() {
        composeTestRule.setContent { DominosTheme { LoginScreen(onGuestContinue = {}, onLogin = { _, _ -> }, isLoading = false) } }
        composeTestRule.onNodeWithText("Sign In").assertIsEnabled()
    }
    @Test fun acceptsEmailInput() {
        composeTestRule.setContent { DominosTheme { LoginScreen(onGuestContinue = {}, onLogin = { _, _ -> }, isLoading = false) } }
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("test@example.com").assertIsDisplayed()
    }
}
