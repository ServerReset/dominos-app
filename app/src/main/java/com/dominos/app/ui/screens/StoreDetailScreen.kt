package com.dominos.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.dominos.app.ui.components.BlooCard
import com.dominos.app.ui.components.BlooGradientBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreDetailScreen(
    store: StoreInfo,
    onViewMenu: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Store", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface, scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer, titleContentColor = MaterialTheme.colorScheme.onSurface, navigationIconContentColor = MaterialTheme.colorScheme.onSurface)
            )
        }
    ) { padding ->
        BlooGradientBackground(modifier = Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                BlooCard(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(48.dp)) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Icon(Icons.Default.Store, null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(24.dp)) }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column { Text(store.addressDescription ?: "Store #${store.storeID ?: ""}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); store.phone?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) } }
                    }
                    if (store.hoursDescription != null) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    store.hoursDescription?.let { Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Schedule, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } }
                    if (store.isOpen) Row(verticalAlignment = Alignment.CenterVertically) { Surface(shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)) { Row(Modifier.padding(horizontal = 10.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(14.dp)); Spacer(Modifier.width(4.dp)); Text("Open now", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary) } } }
                }

                BlooCard {
                    Text("Services", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (store.allowDeliveryOrders) AssistChip(onClick = {}, label = { Text("Delivery") }, leadingIcon = { Icon(Icons.Default.DirectionsCar, null, modifier = Modifier.size(18.dp)) })
                        if (store.allowCarryoutOrders) AssistChip(onClick = {}, label = { Text("Carryout") }, leadingIcon = { Icon(Icons.Default.ShoppingBag, null, modifier = Modifier.size(18.dp)) })
                    }
                    store.estimatedWaitMinutes?.let { wait ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            wait.delivery?.let { d -> if (d.min > 0) Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("${d.min}-${d.max}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary); Text("min delivery", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } }
                            wait.carryout?.let { c -> if (c.min > 0) Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("${c.min}-${c.max}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary); Text("min carryout", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } }
                        }
                    }
                }

                store.storeCoordinates?.let { coords ->
                    BlooCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(12.dp))
                            Column { Text("${coords.latitude ?: "?"}, ${coords.longitude ?: "?"}", style = MaterialTheme.typography.bodyMedium); Text("Tap to open in maps", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        }
                    }
                }

                Button(onClick = onViewMenu, modifier = Modifier.fillMaxWidth().height(56.dp), shape = MaterialTheme.shapes.large, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                    Icon(Icons.Default.RestaurantMenu, null, modifier = Modifier.size(22.dp)); Spacer(Modifier.width(8.dp)); Text("View Menu", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
