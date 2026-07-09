package com.dominos.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dominos.app.ui.components.BooCard
import com.dominos.app.ui.components.BooScreenScaffold
import com.dominos.app.viewmodel.OrderHistoryEntry
import com.dominos.app.viewmodel.OrderHistoryUiState

@Composable
fun OrderHistoryScreen(state: OrderHistoryUiState, onBack: () -> Unit, onReorder: (String) -> Unit = {}) {
    var sel by remember { mutableStateOf<OrderHistoryEntry?>(null) }

    BooScreenScaffold(title = "Order History", onBack = onBack) {
        if (state.orders.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.History, null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(16.dp))
                    Text("No order history", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Place an order to see it here", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.orders, key = { it.orderId + it.timestamp }) { e ->
                    BooCard {
                        Column(Modifier.clickable { sel = e }) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Order #${e.orderId}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text("\$${e.total}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(e.itemsSummary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { onReorder(e.storeId) }, shape = MaterialTheme.shapes.large) { Text("Reorder", fontWeight = FontWeight.Bold) }
                                FilledTonalButton(onClick = { sel = e }, shape = MaterialTheme.shapes.large) { Text("Details", fontWeight = FontWeight.Medium) }
                            }
                        }
                    }
                }
            }
        }
    }

    sel?.let { e ->
        AlertDialog(
            onDismissRequest = { sel = null },
            shape = MaterialTheme.shapes.extraLarge,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            title = { Text("Order #${e.orderId}", fontWeight = FontWeight.Bold) },
            text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Items", fontWeight = FontWeight.Medium); Text(e.itemsSummary, style = MaterialTheme.typography.bodySmall) }
                HorizontalDivider()
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Total", fontWeight = FontWeight.Bold); Text("\$${e.total}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Store", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall); Text("#${e.storeId}", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall) }
            } },
            confirmButton = { Button(onClick = { onReorder(e.storeId); sel = null }) { Text("Reorder") } },
            dismissButton = { TextButton(onClick = { sel = null }) { Text("Close") } }
        )
    }
}
