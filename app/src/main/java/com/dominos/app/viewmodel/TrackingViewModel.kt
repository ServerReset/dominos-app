package com.dominos.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dominos.app.data.repository.DominosRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class TrackingUiState(
    val currentStage: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

class TrackingViewModel : ViewModel() {
    private val repository = DominosRepository()
    private val _uiState = MutableStateFlow(TrackingUiState())
    val uiState: StateFlow<TrackingUiState> = _uiState
    private var pollingJob: Job? = null

    private val stageKeywords = listOf(
        "Placed", "Preparing", "Baking", "Quality", "Delivery", "Delivered"
    )

    fun startTracking(storeId: String, orderKey: String) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            _uiState.value = TrackingUiState(isLoading = true)
            while (isActive) {
                val result = repository.getTrackerData(storeId, orderKey)
                result.fold(
                    onSuccess = { data ->
                        val stage = data.orderStage ?: data.status ?: ""
                        val index = stageKeywords.indexOfFirst { stage.contains(it, ignoreCase = true) }
                        val clamped = if (index >= 0) index else _uiState.value.currentStage
                        _uiState.value = TrackingUiState(currentStage = clamped, isLoading = false)
                        if (clamped >= stageKeywords.size - 1) {
                            _uiState.value = TrackingUiState(currentStage = clamped)
                            pollingJob?.cancel()
                            return@launch
                        }
                    },
                    onFailure = {
                        _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
                    }
                )
                delay(15000)
            }
        }
    }

    fun stopTracking() { pollingJob?.cancel(); _uiState.value = TrackingUiState() }
}
