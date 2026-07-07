package com.dominos.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dominos.app.data.model.CartItem

import com.dominos.app.viewmodel.AccountUiState
import com.dominos.app.viewmodel.OrderUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    storeId: String,
    accountState: AccountUiState,
    orderState: OrderUiState,
    cartItems: List<CartItem>,
    onUpdateField: (String, String) -> Unit,
    onSaveProfile: () -> Unit,
    onPlaceOrder: () -> Unit,
    onServiceMethodChange: (String) -> Unit,
    onViewTracking: (String) -> Unit,
    onBack: () -> Unit,
    onTipChange: (Double) -> Unit = {}
) {
    var couponCode by remember { mutableStateOf("") }
    var tipPercent by remember { mutableIntStateOf(15) }
    val subtotal = cartItems.fold(0.0) { acc, item -> acc + ((item.price?.toDoubleOrNull() ?: 0.0) * item.quantity) }
    val tipAmount = subtotal * tipPercent / 100.0
    LaunchedEffect(tipPercent) { onTipChange(tipAmount) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface, scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer, titleContentColor = MaterialTheme.colorScheme.onSurface, navigationIconContentColor = MaterialTheme.colorScheme.onSurface)
            )
        }
    ) { padding ->
        if (orderState.orderPlacedSuccessfully) {
            Box(Modifier.fillMaxSize().padding(padding).padding(32.dp).background(
                Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f), MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f), MaterialTheme.colorScheme.surface))
            ), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val scale = androidx.compose.animation.core.animateFloatAsState(targetValue = 1f, animationSpec = androidx.compose.animation.core.spring(dampingRatio = 0.3f), label = "success_scale")
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(80.dp * scale.value).then(Modifier.size(80.dp)), tint = MaterialTheme.colorScheme.tertiary)
                    Spacer(Modifier.height(16.dp))
                    Text("Order Placed!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    orderState.estimatedWait?.let { Spacer(Modifier.height(8.dp)); Text("Estimated wait: $it", style = MaterialTheme.typography.bodyLarge) }
                    orderState.pulseOrderGuid?.let { Spacer(Modifier.height(4.dp)); Text("Order #$it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    Spacer(Modifier.height(24.dp))
                    Button(onClick = { onViewTracking(orderState.pulseOrderGuid ?: "") }, shape = MaterialTheme.shapes.large, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) { Text("Track Order", fontWeight = FontWeight.Bold) }
                }
            }
        } else {
            Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ExpressiveSectionCard("Service Method") {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("Delivery", "Carryout", "DriveUpCarryout").forEach { method ->
                            val isSelected = orderState.serviceMethod == method
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface), border = if (isSelected) CardDefaults.outlinedCardBorder().copy(width = 2.dp) else null) {
                                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(selected = isSelected, onClick = { onServiceMethodChange(method) }, colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary))
                                    Spacer(Modifier.width(12.dp)); Text(method, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        }
                    }
                }

                ExpressiveSectionCard("Customer Information") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = accountState.firstName, onValueChange = { onUpdateField("firstName", it) }, label = { Text("First Name") }, modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary))
                            OutlinedTextField(value = accountState.lastName, onValueChange = { onUpdateField("lastName", it) }, label = { Text("Last Name") }, modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary))
                        }
                        OutlinedTextField(value = accountState.phone, onValueChange = { onUpdateField("phone", it) }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(14.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary))
                        OutlinedTextField(value = accountState.email, onValueChange = { onUpdateField("email", it) }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(14.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary))
                        OutlinedTextField(value = accountState.street, onValueChange = { onUpdateField("street", it) }, label = { Text("Street Address") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = accountState.city, onValueChange = { onUpdateField("city", it) }, label = { Text("City") }, modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary))
                            OutlinedTextField(value = accountState.region, onValueChange = { onUpdateField("region", it) }, label = { Text("State") }, modifier = Modifier.width(80.dp), singleLine = true, shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary))
                            OutlinedTextField(value = accountState.postalCode, onValueChange = { onUpdateField("postalCode", it) }, label = { Text("ZIP") }, modifier = Modifier.width(100.dp), singleLine = true, shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary))
                        }
                    }
                }

                ExpressiveSectionCard("Coupon Code") {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(value = couponCode, onValueChange = { couponCode = it }, label = { Text("Enter coupon code") }, modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary))
                        Spacer(Modifier.width(8.dp))
                        FilledTonalButton(onClick = { /* TODO: validate coupon via API */ }, shape = MaterialTheme.shapes.large, modifier = Modifier.height(56.dp)) { Text("Apply", fontWeight = FontWeight.Bold) }
                    }
                }

                ExpressiveSectionCard("Order Summary") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        cartItems.forEach { item ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("${item.quantity}x ${item.productName}", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                                val lineTotal = (item.price?.toDoubleOrNull() ?: 0.0) * item.quantity
                                Text("\$${"%.2f".format(lineTotal)}", fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        HorizontalDivider()
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Subtotal", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text("\$${"%.2f".format(subtotal)}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Tax & Fees", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                            Text("Calculated at store", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                ExpressiveSectionCard("Tip") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(0, 10, 15, 20, 25).forEach { tip ->
                            val selected = tipPercent == tip
                            FilterChip(selected = selected, onClick = { tipPercent = tip },
                                label = { Text(if (tip == 0) "None" else "$tip%", fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
                                shape = MaterialTheme.shapes.large,
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer, selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer))
                        }
                    }
                    if (tipPercent > 0) {
                        Spacer(Modifier.height(8.dp))
                        Text("Tip: \$${"%.2f".format(tipAmount)}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }

                orderState.error?.let {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), shape = MaterialTheme.shapes.large) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp)); Text(it, color = MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                        }
                    }
                }

                Button(onClick = onPlaceOrder, modifier = Modifier.fillMaxWidth().height(56.dp), shape = MaterialTheme.shapes.large, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary), enabled = !orderState.isLoading && accountState.firstName.isNotBlank() && accountState.street.isNotBlank() && cartItems.isNotEmpty()) {
                    if (orderState.isLoading) CircularProgressIndicator(Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                    else Text("Place Order", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ExpressiveSectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
        Column(Modifier.padding(20.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(12.dp)); content()
        }
    }
}
