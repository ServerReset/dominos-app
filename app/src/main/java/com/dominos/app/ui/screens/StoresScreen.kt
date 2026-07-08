package com.dominos.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dominos.app.data.model.StoreInfo
import com.dominos.app.ui.components.BooCard
import com.dominos.app.ui.components.BooScreenScaffold
import com.dominos.app.ui.components.ShimmerStoreCard

@Composable
fun StoresScreen(stores: List<StoreInfo>, onStoreClick: (StoreInfo) -> Unit, onStoreDetailClick: (StoreInfo) -> Unit = {}, onBack: () -> Unit, isLoading: Boolean = false) {
    BooScreenScaffold(title = "Nearby Stores", onBack = onBack) {
        if (isLoading && stores.isEmpty()) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) { items(5) { ShimmerStoreCard() } }
        } else if (stores.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { CircularProgressIndicator(Modifier.size(48.dp), color = MaterialTheme.colorScheme.primary, strokeWidth = 4.dp); Spacer(Modifier.height(16.dp)); Text("Finding stores...", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant) } }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                itemsIndexed(stores) { _, store ->
                    BooCard(onClick = { onStoreClick(s) }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp)); Spacer(Modifier.width(8.dp))
                            Text(store.addressDescription ?: "Store #${store.storeID ?: ""}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                            IconButton(onClick = { onStoreDetailClick(store) }) { Icon(Icons.Default.Info, "Details", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp)) }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text(store.hoursDescription ?: "Hours unavailable", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (store.allowDeliveryOrders) AssistChip(onClick = { onStoreClick(store) }, label = { Text("Delivery") }, shape = MaterialTheme.shapes.medium)
                            if (store.allowCarryoutOrders) AssistChip(onClick = { onStoreClick(store) }, label = { Text("Carryout") }, shape = MaterialTheme.shapes.medium)
                        }
                        store.estimatedWaitMinutes?.let { wait -> Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { wait.delivery?.let { d -> if (d.min > 0) Text("Delivery: ${d.min}-${d.max}min", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary) }; wait.carryout?.let { c -> if (c.min > 0) Text("Carryout: ${c.min}-${c.max}min", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary) } } }
                    }
                }
            }
        }
    }
}
