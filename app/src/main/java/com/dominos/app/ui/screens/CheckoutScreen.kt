package com.dominos.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dominos.app.data.model.CartItem
import com.dominos.app.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    storeId: String, accountState: AccountUiState, orderState: OrderUiState,
    cartItems: List<CartItem>, onUpdateField: (String, String) -> Unit,
    onSaveProfile: () -> Unit, onPlaceOrder: () -> Unit,
    onServiceMethodChange: (String) -> Unit, onViewTracking: (String) -> Unit,
    onBack: () -> Unit, onTipChange: (Double) -> Unit = {}
) {
    var cc by remember { mutableStateOf("") }
    var tp by remember { mutableIntStateOf(15) }
    val sub = cartItems.fold(0.0) { a, i -> a + ((i.price?.toDoubleOrNull() ?: 0.0) * i.quantity) }
    val ta = sub * tp / 100.0
    LaunchedEffect(tp) { onTipChange(ta) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface, scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface, navigationIconContentColor = MaterialTheme.colorScheme.onSurface)
            )
        }
    ) { padding ->
        if (orderState.orderPlacedSuccessfully) {
            val ss = animateFloatAsState(1f, spring(dampingRatio = 0.3f, stiffness = 200f), label = "s")
            val ro by animateFloatAsState(if (ss.value > 0.5f) 1f else 0f, tween(300), label = "r")
            Box(Modifier.fillMaxSize().padding(padding).padding(32.dp)
                .background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f), MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.15f), MaterialTheme.colorScheme.surface))),
                contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size((80 * ss.value).dp).rotate(ro * 360f), tint = MaterialTheme.colorScheme.tertiary)
                    Spacer(Modifier.height(16.dp)); Text("Order Placed!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    orderState.estimatedWait?.let { Spacer(Modifier.height(8.dp)); Text("Est: $it") }
                    orderState.pulseOrderGuid?.let { Spacer(Modifier.height(4.dp)); Text("#$it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    Spacer(Modifier.height(24.dp))
                    Button(onClick = { onViewTracking(orderState.pulseOrderGuid ?: "") }) { Text("Track Order", fontWeight = FontWeight.Bold) }
                }
            }
        } else {
            Box(Modifier.fillMaxSize().padding(padding).background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.surfaceContainerHigh, MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surfaceContainerLow)))) {
                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionCard("Service Method") {
                        listOf("Delivery", "Carryout", "DriveUpCarryout").forEach { m ->
                            val sel = orderState.serviceMethod == m
                            Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium,
                                colors = CardDefaults.cardColors(containerColor = if (sel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface),
                                border = if (sel) CardDefaults.outlinedCardBorder().copy(width = 2.dp) else null) {
                                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(selected = sel, onClick = { onServiceMethodChange(m) })
                                    Spacer(Modifier.width(12.dp)); Text(m, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }
                    }
                    SectionCard("Customer Info") {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = accountState.firstName, onValueChange = { onUpdateField("firstName", it) }, label = { Text("First") }, modifier = Modifier.weight(1f), singleLine = true, shape = MaterialTheme.shapes.medium)
                            OutlinedTextField(value = accountState.lastName, onValueChange = { onUpdateField("lastName", it) }, label = { Text("Last") }, modifier = Modifier.weight(1f), singleLine = true, shape = MaterialTheme.shapes.medium)
                        }
                        OutlinedTextField(value = accountState.phone, onValueChange = { onUpdateField("phone", it) }, label = { Text("Phone") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                        OutlinedTextField(value = accountState.email, onValueChange = { onUpdateField("email", it) }, label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium)
                        OutlinedTextField(value = accountState.street, onValueChange = { onUpdateField("street", it) }, label = { Text("Street") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = accountState.city, onValueChange = { onUpdateField("city", it) }, label = { Text("City") }, modifier = Modifier.weight(1f), singleLine = true, shape = MaterialTheme.shapes.medium)
                            OutlinedTextField(value = accountState.region, onValueChange = { onUpdateField("region", it) }, label = { Text("State") }, modifier = Modifier.width(80.dp), singleLine = true, shape = MaterialTheme.shapes.medium)
                            OutlinedTextField(value = accountState.postalCode, onValueChange = { onUpdateField("postalCode", it) }, label = { Text("ZIP") }, modifier = Modifier.width(100.dp), singleLine = true, shape = MaterialTheme.shapes.medium)
                        }
                    }
                    SectionCard("Coupon") {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(value = cc, onValueChange = { cc = it }, label = { Text("Code") }, modifier = Modifier.weight(1f), singleLine = true, shape = MaterialTheme.shapes.medium)
                            Spacer(Modifier.width(8.dp)); FilledTonalButton(onClick = {}, shape = MaterialTheme.shapes.large, modifier = Modifier.height(56.dp)) { Text("Apply", fontWeight = FontWeight.Bold) }
                        }
                    }
                    SectionCard("Summary") {
                        cartItems.forEach { i -> Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("${i.quantity}x ${i.productName}", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium); Text("\$${"%.2f".format((i.price?.toDoubleOrNull() ?: 0.0) * i.quantity)}", fontWeight = FontWeight.Medium) } }
                        HorizontalDivider()
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Subtotal", fontWeight = FontWeight.Bold); Text("\$${"%.2f".format(sub)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Tax", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall); Text("At store", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall) }
                    }
                    SectionCard("Tip") {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(0, 10, 15, 20, 25).forEach { t ->
                                FilterChip(selected = tp == t, onClick = { tp = t }, label = { Text(if (t == 0) "None" else "$t%", fontWeight = if (tp == t) FontWeight.Bold else FontWeight.Normal) },
                                    shape = MaterialTheme.shapes.large, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer, selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer))
                            }
                        }
                        if (tp > 0) { Spacer(Modifier.height(8.dp)); Text("Tip: \$${"%.2f".format(ta)}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) }
                    }
                    if (orderState.error != null) {
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), shape = MaterialTheme.shapes.large) {
                            Text(orderState.error, modifier = Modifier.padding(12.dp), color = MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Button(onClick = onPlaceOrder, modifier = Modifier.fillMaxWidth().height(56.dp), shape = MaterialTheme.shapes.large,
                        enabled = !orderState.isLoading && accountState.firstName.isNotBlank() && accountState.street.isNotBlank() && cartItems.isNotEmpty()) {
                        if (orderState.isLoading) CircularProgressIndicator(Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                        else Text("Place Order", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
        Column(Modifier.padding(20.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(12.dp)); content()
        }
    }
}
