package com.dominos.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.dominos.app.data.local.LocalStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class FavoritesUiState(
    val favorites: List<FavoriteItem> = emptyList()
)

data class FavoriteItem(val code: String, val name: String)

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {
    private val storage = LocalStorage(application)
    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState

    init { loadFavorites() }

    private fun loadFavorites() {
        val items = storage.getFavorites().mapNotNull { entry ->
            val parts = entry.split("|", limit = 2)
            if (parts.size == 2) FavoriteItem(parts[0], parts[1].ifEmpty { parts[0] })
            else null
        }
        _uiState.value = FavoritesUiState(favorites = items)
    }

    fun toggleFavorite(code: String, name: String = "") {
        storage.toggleFavorite(code, name)
        loadFavorites()
    }

    fun isFavorite(code: String): Boolean = storage.isFavorite(code)
    fun getFavoriteName(code: String): String = storage.getFavoriteName(code)
}
