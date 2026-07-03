package com.dominos.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dominos.app.data.model.StoreInfo
import com.dominos.app.data.model.StoreLocatorResponse
import com.dominos.app.data.repository.DominosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class StoreUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val stores: List<StoreInfo> = emptyList(),
    val selectedStore: StoreInfo? = null,
    val searchQuery: String = "",
    val zipQuery: String = ""
)

class StoreViewModel : ViewModel() {
    private val repository = DominosRepository()
    private val _uiState = MutableStateFlow(StoreUiState())
    val uiState: StateFlow<StoreUiState> = _uiState

    fun searchStores(street: String, zipCode: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.findStores(street, zipCode)
            result.fold(
                onSuccess = { response ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        stores = response.stores ?: emptyList(),
                        searchQuery = street,
                        zipQuery = zipCode
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Search failed"
                    )
                }
            )
        }
    }

    fun selectStore(store: StoreInfo) {
        _uiState.value = _uiState.value.copy(selectedStore = store)
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun updateZipQuery(zip: String) {
        _uiState.value = _uiState.value.copy(zipQuery = zip)
    }
}
