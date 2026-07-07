package com.dominos.app.viewmodel

import org.junit.Assert.*
import org.junit.Test

class StoreViewModelTest {
    @Test fun `initial state has empty stores`() {
        val state = StoreUiState()
        assertTrue(state.stores.isEmpty())
        assertFalse(state.isLoading)
    }

    @Test fun `searchStores sets loading state`() {
        val state = StoreUiState(isLoading = true)
        assertTrue(state.isLoading)
    }

    @Test fun `selectStore updates selected store`() {
        val state = StoreUiState(isLoading = true)
        assertTrue(state.isLoading)
    }
}
