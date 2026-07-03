package com.dominos.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.dominos.app.ui.theme.*
import com.dominos.app.ui.theme.*
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
    onBack: () -> Unit
) {
    val subtotal = cartItems.fold(0.0) { acc, item ->
        val p = item.price?.toDoubleOrNull() ?: 0.0
        acc + (p * item.quantity)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(@Suppress("DEPRECATION") Icons.Default.ArrowBack, contentDescription = "Back")
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
        if (orderState.orderPlacedSuccessfully) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = DominosGreen
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Order Placed!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    orderState.estimatedWait?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Estimated wait: $it", style = MaterialTheme.typography.bodyLarge)
                    }
                    orderState.pulseOrderGuid?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Order #$it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { onViewTracking(orderState.pulseOrderGuid ?: "") },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DominosRed)
                    ) {
                        Text("Track Order", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ExpressiveSection("Service Method") {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("Delivery", "Carryout", "DriveUpCarryout").forEach { method ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (orderState.serviceMethod == method)
                                        DominosRed.copy(alpha = 0.08f)
                                    else MaterialTheme.colorScheme.surface
                                ),
                                border = if (orderState.serviceMethod == method)
                                    CardDefaults.outlinedCardBorder().copy(width = 2.dp) else null
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = orderState.serviceMethod == method,
                                        onClick = { onServiceMethodChange(method) },
                                        colors = RadioButtonDefaults.colors(selectedColor = DominosRed)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(method, fontWeight = if (orderState.serviceMethod == method) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }
                    }
                }

                ExpressiveSection("Customer Information") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = accountState.firstName,
                                onValueChange = { onUpdateField("firstName", it) },
                                label = { Text("First Name") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = accountState.lastName,
                                onValueChange = { onUpdateField("lastName", it) },
                                label = { Text("Last Name") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        OutlinedTextField(
                            value = accountState.phone,
                            onValueChange = { onUpdateField("phone", it) },
                            label = { Text("Phone") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )

                        OutlinedTextField(
                            value = accountState.email,
                            onValueChange = { onUpdateField("email", it) },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )

                        OutlinedTextField(
                            value = accountState.street,
                            onValueChange = { onUpdateField("street", it) },
                            label = { Text("Street Address") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = accountState.city,
                                onValueChange = { onUpdateField("city", it) },
                                label = { Text("City") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = accountState.region,
                                onValueChange = { onUpdateField("region", it) },
                                label = { Text("State") },
                                modifier = Modifier.width(80.dp),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = accountState.postalCode,
                                onValueChange = { onUpdateField("postalCode", it) },
                                label = { Text("ZIP") },
                                modifier = Modifier.width(100.dp),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }

                ExpressiveSection("Order Summary") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        cartItems.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${item.quantity}x ${item.productName}", modifier = Modifier.weight(1f))
                                val lineTotal = (item.price?.toDoubleOrNull() ?: 0.0) * item.quantity
                                Text("\$${"%.2f".format(lineTotal)}", fontWeight = FontWeight.Medium)
                            }
                        }
                        HorizontalDivider()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Subtotal", fontWeight = FontWeight.Bold)
                            Text("\$${"%.2f".format(subtotal)}", fontWeight = FontWeight.Bold, color = DominosRed)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Tax & Fees", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Calculated at store", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                        }
                    }
                }

                orderState.error?.let {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            it,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 14.sp
                        )
                    }
                }

                Button(
                    onClick = onPlaceOrder,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DominosRed),
                    enabled = !orderState.isLoading && accountState.firstName.isNotBlank() && accountState.street.isNotBlank() && cartItems.isNotEmpty()
                ) {
                    if (orderState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Place Order", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ExpressiveSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DominosRed
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}
