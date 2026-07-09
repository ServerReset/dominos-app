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
fun StoresScreen(
    stores: List<StoreInfo>,
    onStoreClick: (StoreInfo) -> Unit,
    onStoreDetailClick: (StoreInfo) -> Unit = {},
    onBack: () -> Unit,
    isLoading: Boolean = false
) {
    BooScreenScaffold(title = "Nearby Stores", onBack = onBack) {
        if (isLoading && stores.isEmpty()) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(5) { ShimmerStoreCard() }
            }
        } else if (stores.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(Modifier.size(48.dp), strokeWidth = 4.dp)
                    Spacer(Modifier.height(16.dp))
                    Text("Finding stores...", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                itemsIndexed(stores) { _, s ->
                    BooCard(onClick = { onStoreClick(s) }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(s.addressDescription ?: "Store #${s.storeID ?: ""}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            IconButton(onClick = { onStoreDetailClick(s) }) { Icon(Icons.Default.Info, "Details", modifier = Modifier.size(20.dp)) }
                        }
                        Row {
                            Icon(Icons.Default.Schedule, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp)); Text(s.hoursDescription ?: "Hours unavailable", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (s.allowDeliveryOrders) AssistChip(onClick = { onStoreClick(s) }, label = { Text("Delivery") })
                            if (s.allowCarryoutOrders) AssistChip(onClick = { onStoreClick(s) }, label = { Text("Carryout") })
                        }
                        s.estimatedWaitMinutes?.let { w ->
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                w.delivery?.let { d -> if (d.min > 0) Text("Delivery: ${d.min}-${d.max}min", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary) }
                                w.carryout?.let { c -> if (c.min > 0) Text("Carryout: ${c.min}-${c.max}min", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary) }
                            }
                        }
                    }
                }
            }
        }
    }
}
