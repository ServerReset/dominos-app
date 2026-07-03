package com.dominos.app.viewmodel

import org.junit.Assert.*
import org.junit.Test

class AccountViewModelTest {
    @Test fun `uiState initializes empty`() {
        val state = AccountUiState()
        assertEquals("", state.firstName); assertEquals("", state.lastName); assertEquals("", state.email); assertFalse(state.isLoggedIn)
    }
    @Test fun `updateField sets fields`() {
        assertEquals("John", AccountUiState().copy(firstName = "John").firstName)
        assertEquals("Doe", AccountUiState().copy(lastName = "Doe").lastName)
        assertEquals("john@example.com", AccountUiState().copy(email = "john@example.com").email)
        assertEquals("555-0100", AccountUiState().copy(phone = "555-0100").phone)
        assertEquals("123 Main St", AccountUiState().copy(street = "123 Main St").street)
    }
    @Test fun `toggleDarkMode flips state`() {
        assertTrue(AccountUiState(isDarkMode = false).copy(isDarkMode = true).isDarkMode)
        assertFalse(AccountUiState(isDarkMode = true).copy(isDarkMode = false).isDarkMode)
    }
}
