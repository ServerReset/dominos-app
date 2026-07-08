package com.dominos.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dominos.app.data.model.StoreInfo
import com.dominos.app.ui.components.ShimmerStoreCard


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoresScreen(
    stores: List<StoreInfo>,
    onStoreClick: (StoreInfo) -> Unit,
    onStoreDetailClick: (StoreInfo) -> Unit = {},
    onBack: () -> Unit,
    isLoading: Boolean = false
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nearby Stores", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface, scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer)
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().background(brush = Brush.verticalGradient(listOf(MaterialTheme.colorScheme.surfaceContainerHigh, MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surfaceContainerLow)))) {
        if (isLoading && stores.isEmpty()) {
            LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp), contentPadding = PaddingValues(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(5) { ShimmerStoreCard() }
            }
        } else if (stores.isEmpty() && !isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Store, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(16.dp)); Text("No stores found", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp)); Text("Try a different ZIP code", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else if (stores.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(Modifier.size(48.dp), color = MaterialTheme.colorScheme.primary, strokeWidth = 4.dp)
                    Spacer(Modifier.height(16.dp)); Text("Finding stores...", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(vertical = 16.dp)) {
                itemsIndexed(stores) { _, store ->
                    Card(onClick = { onStoreClick(store) }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(store.addressDescription ?: "Store #${store.storeID ?: ""}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                                IconButton(onClick = { onStoreDetailClick(store) }) { Icon(Icons.Default.Info, contentDescription = "Details", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp)) }
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(store.hoursDescription ?: "Hours unavailable", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (store.allowDeliveryOrders) AssistChip(onClick = { onStoreClick(store) }, label = { Text("Delivery") }, shape = RoundedCornerShape(12.dp))
                                if (store.allowCarryoutOrders) AssistChip(onClick = { onStoreClick(store) }, label = { Text("Carryout") }, shape = RoundedCornerShape(12.dp))
                            }
                            store.estimatedWaitMinutes?.let { wait ->
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    wait.delivery?.let { d -> if (d.min > 0) Text("Delivery: ${d.min}-${d.max}min", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary) }
                                    wait.carryout?.let { c -> if (c.min > 0) Text("Carryout: ${c.min}-${c.max}min", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
        }
}
