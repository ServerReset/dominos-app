package com.dominos.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dominos.app.data.model.EstimatedWaitMinutes
import com.dominos.app.data.model.ServiceIsOpen
import com.dominos.app.data.model.StoreInfo
import com.dominos.app.ui.theme.DominosGreen
import com.dominos.app.ui.theme.DominosRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoresScreen(
    stores: List<StoreInfo>,
    isLoading: Boolean,
    onStoreSelected: (String) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nearby Stores") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DominosRed,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = DominosRed
                )
            } else if (stores.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Store,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No stores found nearby",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(stores) { _, store ->
                        StoreDetailCard(store = store, onClick = { onStoreSelected(store.storeID ?: "") })
                    }
                }
            }
        }
    }
}

@Composable
fun StoreDetailCard(store: StoreInfo, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Store #${store.storeID ?: "?"}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (store.isOpen) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = DominosGreen.copy(alpha = 0.15f)
                    ) {
                        Text(
                            "OPEN",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            color = DominosGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            "CLOSED",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            store.addressDescription?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                store.estimatedWaitMinutes?.let { wait ->
                    WaitTimeDisplay("Delivery", wait.delivery?.min, wait.delivery?.max)
                    WaitTimeDisplay("Carryout", wait.carryout?.min, wait.carryout?.max)
                }
                store.serviceIsOpen?.let { open ->
                    if (open.delivery) DeliveryIcon()
                    if (open.carryout) CarryoutIcon()
                }
            }

            store.hoursDescription?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun WaitTimeDisplay(label: String, min: Int?, max: Int?) {
    if (min != null && max != null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Schedule,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "${min}-${max} min",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DeliveryIcon() {
    Icon(
        Icons.Default.DirectionsCar,
        contentDescription = "Delivery",
        modifier = Modifier.size(16.dp),
        tint = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun CarryoutIcon() {
    Icon(
        Icons.Default.ShoppingBag,
        contentDescription = "Carryout",
        modifier = Modifier.size(16.dp),
        tint = MaterialTheme.colorScheme.primary
    )
}
