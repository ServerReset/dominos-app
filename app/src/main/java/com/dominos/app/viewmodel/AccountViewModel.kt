package com.dominos.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dominos.app.data.local.LocalStorage
import com.dominos.app.data.model.SavedCustomer
import com.dominos.app.data.repository.DominosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AccountUiState(
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val email: String = "",
    val street: String = "",
    val city: String = "",
    val region: String = "",
    val postalCode: String = "",
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val isDarkMode: Boolean = false,
    val loginSuccess: Boolean = false,
    val error: String? = null
)

class AccountViewModel(application: Application) : AndroidViewModel(application) {
    private val storage = LocalStorage(application)
    private val repository = DominosRepository()
    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState

    init { loadSavedProfile() }

    private fun loadSavedProfile() {
        val saved = storage.getCustomer()
        _uiState.value = AccountUiState(
            firstName = saved.firstName, lastName = saved.lastName, phone = saved.phone,
            email = saved.email, street = saved.street, city = saved.city,
            region = saved.region, postalCode = saved.postalCode,
            isLoggedIn = storage.isLoggedIn(), isDarkMode = storage.isDarkMode()
        )
    }

    fun updateField(field: String, value: String) {
        _uiState.value = when (field) {
            "firstName" -> _uiState.value.copy(firstName = value, error = null)
            "lastName" -> _uiState.value.copy(lastName = value, error = null)
            "phone" -> _uiState.value.copy(phone = value, error = null)
            "email" -> _uiState.value.copy(email = value, error = null)
            "street" -> _uiState.value.copy(street = value, error = null)
            "city" -> _uiState.value.copy(city = value, error = null)
            "region" -> _uiState.value.copy(region = value, error = null)
            "postalCode" -> _uiState.value.copy(postalCode = value, error = null)
            else -> _uiState.value
        }
    }

    fun saveProfile() {
        val state = _uiState.value
        storage.saveCustomer(SavedCustomer(
            firstName = state.firstName, lastName = state.lastName, phone = state.phone,
            email = state.email, street = state.street, city = state.city,
            region = state.region, postalCode = state.postalCode, isLoggedIn = true
        ))
        _uiState.value = _uiState.value.copy(isLoggedIn = true, error = null)
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, loginSuccess = false)
            val result = repository.login(email, password)
            result.fold(
                onSuccess = {
                    storage.saveCustomer(SavedCustomer(email = email, isLoggedIn = true))
                    _uiState.value = _uiState.value.copy(
                        email = email, isLoggedIn = true, isLoading = false, loginSuccess = true, error = null
                    )
                },
                onFailure = { e ->
                    val msg = e.message ?: "Login failed"
                    _uiState.value = _uiState.value.copy(
                        isLoading = false, error = "Sign in unavailable: $msg. Continue as Guest.",
                        loginSuccess = false
                    )
                }
            )
        }
    }

    fun guestContinue() {
        storage.saveCustomer(SavedCustomer(isLoggedIn = true))
        _uiState.value = _uiState.value.copy(isLoggedIn = true)
    }

    fun logout() { storage.logout(); _uiState.value = AccountUiState() }

    fun getCustomerForOrder(): SavedCustomer = storage.getCustomer()

    fun toggleDarkMode() {
        val newVal = !_uiState.value.isDarkMode
        storage.setDarkMode(newVal)
        _uiState.value = _uiState.value.copy(isDarkMode = newVal)
    }

    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }
}
