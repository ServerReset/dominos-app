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
import com.dominos.app.data.model.CartItem
import com.dominos.app.ui.theme.*
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

    val selectedVariant = item.variants.find {
        it.sizeCode == selectedSizeCode && (it.flavorCode == selectedFlavorCode || it.flavorCode == null)
    }
    val basePrice = selectedVariant?.price?.toDoubleOrNull()
        ?: item.price?.removePrefix("$")?.toDoubleOrNull() ?: 0.0
    val totalPrice = basePrice * quantity

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        item.productName,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            @Suppress("DEPRECATION") Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 4.dp,
                shadowElevation = 12.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FilledIconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            modifier = Modifier.size(40.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease")
                        }
                        Text(
                            "$quantity",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        FilledIconButton(
                            onClick = { quantity++ },
                            modifier = Modifier.size(40.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase")
                        }
                    }
                    Button(
                        onClick = {
                            val options = mutableMapOf<String, Map<String, String>>()
                            if (isPizza && selectedSizeCode.isNotEmpty()) {
                                options["X"] = mapOf("1/1" to "1")
                                options["C"] = mapOf(
                                    "1/1" to selectedSizeCode.replace(Regex("[^0-9]"), "")
                                )
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
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            "Add to Cart - \$" + "%.2f".format(totalPrice),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (isPizza && item.sizes.isNotEmpty()) {
                item {
                    ExpressiveSectionHeader("Size")
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        item.sizes.forEach { size ->
                            val variant = item.variants.find { it.sizeCode == size.code }
                            val sizePrice = variant?.price?.toDoubleOrNull()
                            val isSelected = selectedSizeCode == size.code
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surface
                                ),
                                border = if (isSelected)
                                    CardDefaults.outlinedCardBorder().copy(width = 2.dp) else null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedSizeCode = size.code ?: "" },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        size.name ?: size.code ?: "",
                                        modifier = Modifier.weight(1f),
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    sizePrice?.let {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer
                                        ) {
                                            Text(
                                                "\$${"%.2f".format(it)}",
                                                modifier = Modifier.padding(
                                                    horizontal = 12.dp,
                                                    vertical = 4.dp
                                                ),
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
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
                    ExpressiveSectionHeader("Crust")
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        item.flavors.forEach { flavor ->
                            val isSelected = selectedFlavorCode == flavor.code
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedFlavorCode = flavor.code },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        flavor.name ?: flavor.code ?: "",
                                        modifier = Modifier.weight(1f),
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    flavor.price?.toDoubleOrNull()?.let {
                                        if (it > 0) {
                                            Text(
                                                "+\$${"%.2f".format(it)}",
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (item.availableToppings.isNotEmpty()) {
                item { HorizontalDivider() }
                item {
                    ExpressiveSectionHeader("Toppings")
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        item.availableToppings.take(20).forEach { topping ->
                            val isChecked = topping.code in selectedToppings
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isChecked)
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
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
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                    Text(
                                        topping.name ?: topping.code ?: "",
                                        modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    topping.price?.toDoubleOrNull()?.let {
                                        if (it > 0) {
                                            Text(
                                                "+\$${"%.2f".format(it)}",
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Medium
                                            )
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
}

@Composable
private fun ExpressiveSectionHeader(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.weight(1f))
    }
}
