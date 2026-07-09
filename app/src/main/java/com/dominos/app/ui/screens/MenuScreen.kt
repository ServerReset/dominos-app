package com.dominos.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dominos.app.ui.components.BooScreenScaffold
import com.dominos.app.ui.components.DominosProductImage
import com.dominos.app.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    state: MenuUiState,
    onCategorySelected: (Int) -> Unit,
    onProductClick: (String) -> Unit,
    onCartClick: () -> Unit,
    onBack: () -> Unit,
    cartItemCount: Int,
    onToggleFavorite: (String, String) -> Unit = { _, _ -> },
    isFavorite: (String) -> Boolean = { false },
    onRetry: () -> Unit = {},
    onQuickAdd: (String, String, String) -> Unit = { _, _, _ -> }
) {
    var q by remember { mutableStateOf("") }
    val ls = rememberLazyListState()
    val fab by remember { derivedStateOf { ls.firstVisibleItemIndex > 3 } }
    val sc = rememberCoroutineScope()
    val fp = if (q.isBlank()) state.selectedCategoryProducts
        else state.selectedCategoryProducts.filter { it.productName.contains(q, true) || (it.description?.contains(q, true) == true) }

    BooScreenScaffold(title = "Menu", onBack = onBack) {
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                OutlinedTextField(
                    value = q, onValueChange = { q = it },
                    placeholder = { Text("Search...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = MaterialTheme.shapes.medium
                )
                when {
                    state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column { CircularProgressIndicator(Modifier.size(48.dp), strokeWidth = 4.dp); Spacer(Modifier.height(16.dp)); Text("Loading...", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    }
                    state.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Warning, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(16.dp)); Text(state.error, color = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(16.dp)); Button(onClick = onRetry, shape = MaterialTheme.shapes.large) { Text("Retry") }
                        }
                    }
                    else -> PullToRefreshBox(isRefreshing = false, onRefresh = onRetry) {
                        Column {
                            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                itemsIndexed(state.flatCategories) { i, c ->
                                    val sel = i == state.selectedCategoryIndex
                                    FilterChip(selected = sel, onClick = { onCategorySelected(i) },
                                        label = { Text(c.name ?: "", maxLines = 1, fontSize = 13.sp, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal) },
                                        shape = MaterialTheme.shapes.large,
                                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer, selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer))
                                }
                            }
                            HorizontalDivider(thickness = 0.5.dp)
                            if (fp.isEmpty()) {
                                Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.Restaurant, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(Modifier.height(16.dp)); Text("No items found", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            } else {
                                LazyColumn(state = ls, contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    itemsIndexed(fp, key = { _, i -> i.productCode }) { _, i ->
                                        Card(onClick = { onProductClick(i.productCode) }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large,
                                            elevation = CardDefaults.cardElevation(2.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
                                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                                DominosProductImage(productCode = i.imageCode ?: i.productCode, modifier = Modifier.size(72.dp))
                                                Spacer(Modifier.width(16.dp))
                                                Column(Modifier.weight(1f)) {
                                                    Text(i.productName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                                    i.description?.let { Spacer(Modifier.height(4.dp)); Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis) }
                                                    if (i.sizes.isNotEmpty()) {
                                                        Spacer(Modifier.height(6.dp)); Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                            i.sizes.take(3).forEach { s -> Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant) { Text(s.name ?: "", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant) } }
                                                        }
                                                    }
                                                }
                                                Column(horizontalAlignment = Alignment.End) {
                                                    IconButton(onClick = { onToggleFavorite(i.productCode, i.productName) }, modifier = Modifier.size(32.dp)) {
                                                        Icon(if (isFavorite(i.productCode)) Icons.Default.Favorite else Icons.Default.FavoriteBorder, null,
                                                            tint = if (isFavorite(i.productCode)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                                                    }
                                                    i.price?.let { p ->
                                                        Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.primaryContainer) { Text(p, modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer) }
                                                        Spacer(Modifier.height(4.dp))
                                                        FilledIconButton(onClick = { onQuickAdd(i.productCode, i.productName, p) }, modifier = Modifier.size(32.dp), shape = RoundedCornerShape(12.dp), colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary)) { Icon(Icons.Default.Add, "Add", modifier = Modifier.size(16.dp)) }
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
            if (fab) {
                SmallFloatingActionButton(
                    onClick = { sc.launch { ls.animateScrollToItem(0) } },
                    shape = RoundedCornerShape(16.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp).padding(bottom = 80.dp)
                ) { Icon(Icons.Default.KeyboardArrowUp, "Top") }
            }
        }
    }
}
