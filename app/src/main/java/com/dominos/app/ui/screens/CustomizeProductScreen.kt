package com.dominos.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dominos.app.data.model.CartItem
import com.dominos.app.ui.components.DominosProductImage

import com.dominos.app.viewmodel.MenuDisplayItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizeProductScreen(
    storeId: String,
    item: MenuDisplayItem?,
    onAddToCart: (CartItem, String) -> Unit,
    onBack: () -> Unit,
    onToggleFavorite: (String, String) -> Unit = { _, _ -> },
    isFavorite: (String) -> Boolean = { false }
) {
    if (item == null) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Product not found") }; return }

    var selectedSizeCode by remember { mutableStateOf(item.sizes.getOrNull(0)?.code ?: "") }
    var selectedFlavorCode by remember { mutableStateOf<String?>(null) }
    var selectedToppings by remember { mutableStateOf<Set<String>>(emptySet()) }
    var quantity by remember { mutableIntStateOf(1) }

    val isPizza = item.productType == "Pizza" || item.productCode.contains("P") || item.sizes.size > 1
    val selectedVariant = item.variants.find { it.sizeCode == selectedSizeCode && (it.flavorCode == selectedFlavorCode || it.flavorCode == null) }
    val basePrice = selectedVariant?.price?.toDoubleOrNull() ?: item.price?.removePrefix("$")?.toDoubleOrNull() ?: 0.0
    val totalPrice = basePrice * quantity
    val fav = isFavorite(item.productCode)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(item.productName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                actions = {
                    IconButton(onClick = { onToggleFavorite(item.productCode, item.productName) }) {
                        Icon(if (fav) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = "Toggle favorite", tint = if (fav) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface, scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer, titleContentColor = MaterialTheme.colorScheme.onSurface, navigationIconContentColor = MaterialTheme.colorScheme.onSurface)
            )
        },
        bottomBar = {
            Surface(tonalElevation = 4.dp, shadowElevation = 12.dp, color = MaterialTheme.colorScheme.surface) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FilledIconButton(onClick = { if (quantity > 1) quantity-- }, modifier = Modifier.size(40.dp), shape = RoundedCornerShape(14.dp), colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) { Icon(Icons.Default.Remove, contentDescription = "Decrease") }
                        Text("$quantity", modifier = Modifier.padding(horizontal = 16.dp), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        FilledIconButton(onClick = { quantity++ }, modifier = Modifier.size(40.dp), shape = RoundedCornerShape(14.dp), colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)) { Icon(Icons.Default.Add, contentDescription = "Increase") }
                    }
                    Button(onClick = {
                        val options = mutableMapOf<String, Map<String, String>>()
                        if (isPizza && selectedSizeCode.isNotEmpty()) { options["X"] = mapOf("1/1" to "1"); options["C"] = mapOf("1/1" to selectedSizeCode.replace(Regex("[^0-9]"), "")) }
                        selectedToppings.forEach { code -> options[code] = mapOf("1/1" to "1") }
                        onAddToCart(CartItem(productCode = item.productCode, productName = item.productName, quantity = quantity, price = "%.2f".format(totalPrice), options = options.ifEmpty { null }, sizeCode = selectedSizeCode.ifEmpty { null }, flavorCode = selectedFlavorCode, id = 0), storeId)
                    }, shape = MaterialTheme.shapes.large, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                        Text("Add to Cart - \$" + "%.2f".format(totalPrice), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp), contentPadding = PaddingValues(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) { DominosProductImage(productCode = item.imageCode ?: item.productCode, modifier = Modifier.size(160.dp)) }
                Spacer(Modifier.height(8.dp))
                item.description?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                item.tags?.let { tags -> tags["Calories"]?.toString()?.let { cals -> Text("$cals cal", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) } }
            }

            if (isPizza && item.sizes.isNotEmpty()) {
                item { ExpressiveSectionHeader("Size"); Spacer(Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        item.sizes.forEach { size ->
                            val variant = item.variants.find { it.sizeCode == size.code }; val sizePrice = variant?.price?.toDoubleOrNull(); val isSelected = selectedSizeCode == size.code
                            Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface), border = if (isSelected) CardDefaults.outlinedCardBorder().copy(width = 2.dp) else null) {
                                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(selected = isSelected, onClick = { selectedSizeCode = size.code ?: "" }, colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary))
                                    Spacer(Modifier.width(12.dp)); Text(size.name ?: size.code ?: "", modifier = Modifier.weight(1f), fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, style = MaterialTheme.typography.bodyLarge)
                                    sizePrice?.let { Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.primaryContainer) { Text("\$${"%.2f".format(it)}", modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer) } }
                                }
                            }
                        }
                    }
                }
            }

            if (item.flavors.isNotEmpty()) {
                item { ExpressiveSectionHeader("Crust"); Spacer(Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        item.flavors.forEach { flavor ->
                            val isSelected = selectedFlavorCode == flavor.code
                            Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)) {
                                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(selected = isSelected, onClick = { selectedFlavorCode = flavor.code }, colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary))
                                    Spacer(Modifier.width(12.dp)); Text(flavor.name ?: flavor.code ?: "", modifier = Modifier.weight(1f), fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, style = MaterialTheme.typography.bodyLarge)
                                    flavor.price?.toDoubleOrNull()?.let { if (it > 0) Text("+\$${"%.2f".format(it)}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium) }
                                }
                            }
                        }
                    }
                }
            }

            if (item.availableToppings.isNotEmpty()) {
                item { ExpressiveSectionHeader("Toppings"); Spacer(Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        item.availableToppings.take(20).forEach { topping ->
                            val isChecked = topping.code in selectedToppings
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = if (isChecked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))) {
                                Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = isChecked, onCheckedChange = { checked -> selectedToppings = if (checked) selectedToppings + (topping.code ?: "") else selectedToppings - (topping.code ?: "") }, colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary))
                                    Text(topping.name ?: topping.code ?: "", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                                    topping.price?.toDoubleOrNull()?.let { if (it > 0) Text("+\$${"%.2f".format(it)}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium) }
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
private fun ExpressiveSectionHeader(text: String) { Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) }
