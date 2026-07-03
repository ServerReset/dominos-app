package com.dominos.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dominos.app.data.model.*
import com.dominos.app.ui.theme.DominosRed
import com.dominos.app.viewmodel.MenuDisplayItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizeProductScreen(
    storeId: String,
    item: MenuDisplayItem?,
    onAddToCart: (CartItem, String) -> Unit,
    onBack: () -> Unit
) {
    if (item == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Product not found")
        }
        return
    }

    var selectedSizeCode by remember { mutableStateOf(item.sizes.getOrNull(0)?.code ?: "") }
    var selectedFlavorCode by remember { mutableStateOf<String?>(null) }
    var selectedToppings by remember { mutableStateOf<Set<String>>(emptySet()) }
    var quantity by remember { mutableIntStateOf(1) }

    val isPizza = item.productType == "Pizza" || item.productCode.contains("P") || item.sizes.size > 1

    val selectedVariant = item.variants.find { it.sizeCode == selectedSizeCode && (it.flavorCode == selectedFlavorCode || it.flavorCode == null) }
    val basePrice = selectedVariant?.price?.toDoubleOrNull() ?: item.price?.removePrefix("$")?.toDoubleOrNull() ?: 0.0
    val totalPrice = basePrice * quantity

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(item.productName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        @Suppress("DEPRECATION")
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DominosRed,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 3.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (quantity > 1) quantity-- }) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease")
                        }
                        Text("$quantity", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        IconButton(onClick = { quantity++ }) {
                            Icon(Icons.Default.Add, contentDescription = "Increase")
                        }
                    }
                    Button(
                        onClick = {
                            val options = mutableMapOf<String, Map<String, String>>()
                            if (isPizza && selectedSizeCode.isNotEmpty()) {
                                options["X"] = mapOf("1/1" to "1")
                                options["C"] = mapOf("1/1" to selectedSizeCode.replace(Regex("[^0-9]"), ""))
                            }
                            selectedToppings.forEach { code ->
                                options[code] = mapOf("1/1" to "1")
                            }
                            val cartItem = CartItem(
                                productCode = item.productCode,
                                productName = item.productName,
                                quantity = quantity,
                                price = "%.2f".format(totalPrice),
                                options = options.ifEmpty { null },
                                sizeCode = selectedSizeCode.ifEmpty { null },
                                flavorCode = selectedFlavorCode,
                                id = 0
                            )
                            onAddToCart(cartItem, storeId)
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DominosRed)
                    ) {
                        Text("Add to Cart - \$${"%.2f".format(totalPrice)}")
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isPizza && item.sizes.isNotEmpty()) {
                item {
                    Text("Size", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        item.sizes.forEach { size ->
                            val variant = item.variants.find { it.sizeCode == size.code }
                            val sizePrice = variant?.price?.toDoubleOrNull()
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedSizeCode == size.code,
                                    onClick = { selectedSizeCode = size.code ?: "" }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    size.name ?: size.code ?: "",
                                    modifier = Modifier.weight(1f),
                                    fontWeight = if (selectedSizeCode == size.code) FontWeight.Bold else FontWeight.Normal
                                )
                                sizePrice?.let {
                                    Text("\$${"%.2f".format(it)}")
                                }
                            }
                        }
                    }
                }
            }

            if (isPizza && item.sizes.isNotEmpty()) {
                item { HorizontalDivider() }
            }

            if (item.flavors.isNotEmpty()) {
                item {
                    Text("Crust", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        item.flavors.forEach { flavor ->
                            val isSelected = selectedFlavorCode == flavor.code
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedFlavorCode = flavor.code }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    flavor.name ?: flavor.code ?: "",
                                    modifier = Modifier.weight(1f),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                flavor.price?.toDoubleOrNull()?.let {
                                    if (it > 0) Text("+\$${"%.2f".format(it)}")
                                }
                            }
                        }
                    }
                }
            }

            if (item.availableToppings.isNotEmpty()) {
                item { HorizontalDivider() }
                item {
                    Text("Toppings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        item.availableToppings.take(20).forEach { topping ->
                            val isChecked = topping.code in selectedToppings
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        selectedToppings = if (checked) {
                                            selectedToppings + (topping.code ?: "")
                                        } else {
                                            selectedToppings - (topping.code ?: "")
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(topping.name ?: topping.code ?: "", modifier = Modifier.weight(1f))
                                topping.price?.toDoubleOrNull()?.let {
                                    if (it > 0) Text("+\$${"%.2f".format(it)}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
