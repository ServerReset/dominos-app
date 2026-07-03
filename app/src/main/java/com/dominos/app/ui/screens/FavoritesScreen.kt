package com.dominos.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dominos.app.ui.components.DominosProductImage
import com.dominos.app.viewmodel.FavoritesUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    state: FavoritesUiState,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorites", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface, scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer, titleContentColor = MaterialTheme.colorScheme.onSurface, navigationIconContentColor = MaterialTheme.colorScheme.onSurface)
            )
        }
    ) { padding ->
        if (state.favorites.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding).padding(32.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(16.dp))
                    Text("No favorites yet", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Tap the heart icon on any product to save it here", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                }
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp), contentPadding = PaddingValues(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.favorites, key = { it.code }) { fav ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            DominosProductImage(productCode = fav.code, modifier = Modifier.size(56.dp))
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(fav.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text("Product #${fav.code}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(Icons.Default.Favorite, contentDescription = "Favorite", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}
