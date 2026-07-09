package com.dominos.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSearch: (String, String) -> Unit,
    onCartClick: () -> Unit,
    onAccountClick: () -> Unit,
    cartItemCount: Int,
    recentOrderCount: Int = 0
) {
    val ctx = LocalContext.current
    val storage = remember { com.dominos.app.data.local.LocalStorage(ctx) }
    var street by remember { mutableStateOf(storage.getLastStreet()) }
    var zip by remember { mutableStateOf(storage.getLastZipCode()) }
    var locating by remember { mutableStateOf(false) }

    val permLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            locating = true
            try {
                val lm = ctx.getSystemService(android.content.Context.LOCATION_SERVICE) as android.location.LocationManager
                val p = lm.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER) ?: lm.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER)
                if (p != null) {
                    val a = Geocoder(ctx, Locale.getDefault()).getFromLocation(p.latitude, p.longitude, 1) ?: emptyList()
                    if (a.isNotEmpty()) { zip = a[0].postalCode ?: ""; street = "${a[0].thoroughfare ?: ""} ${a[0].subThoroughfare ?: ""}".trim() }
                }
            } catch (_: Exception) {}
            locating = false
        }
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("OpenPizza", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = { IconButton(onClick = onAccountClick) { Icon(Icons.Default.AccountCircle, "Account") } },
                actions = { BadgedBox(badge = { if (cartItemCount > 0) Badge { Text("$cartItemCount") } }) { IconButton(onClick = onCartClick) { Icon(Icons.Default.ShoppingCart, "Cart") } } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface, scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer)
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            // Hero section
            Box(
                Modifier.fillMaxWidth().height(180.dp)
                    .background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)))
            ) {
                Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
                    Text("Craving Pizza?", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                    Text("Find a store near you", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                }
            }

            // Search card overlapping hero
            Card(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp).offset(y = (-20).dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Find your store", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = street, onValueChange = { street = it }, label = { Text("Street") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = zip, onValueChange = { zip = it }, label = { Text("ZIP") }, singleLine = true, modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.medium)
                        FilledIconButton(onClick = {
                            if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                locating = true; try { val lm = ctx.getSystemService(android.content.Context.LOCATION_SERVICE) as android.location.LocationManager
                                    val p = lm.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER) ?: lm.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER)
                                    if (p != null) { val a = Geocoder(ctx, Locale.getDefault()).getFromLocation(p.latitude, p.longitude, 1) ?: emptyList(); if (a.isNotEmpty()) { zip = a[0].postalCode ?: ""; street = "${a[0].thoroughfare ?: ""} ${a[0].subThoroughfare ?: ""}".trim() } }
                                } catch(_: Exception) {}; locating = false
                            } else permLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }, modifier = Modifier.size(56.dp), shape = MaterialTheme.shapes.medium) { Icon(Icons.Default.MyLocation, null) }
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { storage.saveLastStreet(street); storage.saveLastZipCode(zip); onSearch(street, zip) }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = MaterialTheme.shapes.large) {
                        Text("Search", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Content
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Spacer(Modifier.height(8.dp))

                // Quick actions
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    QuickActionCard("Delivery", Icons.Default.DirectionsCar, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                    QuickActionCard("Pickup", Icons.Default.ShoppingBag, MaterialTheme.colorScheme.tertiary, Modifier.weight(1f))
                    QuickActionCard("Deals", Icons.Default.LocalOffer, MaterialTheme.colorScheme.secondary, Modifier.weight(1f))
                }

                // Featured deal
                Card(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Mix & Match Deal", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text("From \$6.99 each", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                            Spacer(Modifier.height(8.dp))
                            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primary) { Text("Order Now", modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp), color = MaterialTheme.colorScheme.onPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                        }
                        Icon(Icons.Default.LocalOffer, null, modifier = Modifier.size(56.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f))
                    }
                }

                if (recentOrderCount > 0) {
                    Card(
                        Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.History, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(32.dp))
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Recent Orders", fontWeight = FontWeight.Bold)
                                Text("$recentOrderCount order${if (recentOrderCount > 1) "s" else ""}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            FilledTonalButton(onClick = onAccountClick, shape = RoundedCornerShape(12.dp)) { Text("View") }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun QuickActionCard(label: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Surface(
        onClick = {},
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier
    ) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(shape = RoundedCornerShape(14.dp), color = color.copy(alpha = 0.12f), modifier = Modifier.size(48.dp)) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Icon(icon, null, tint = color, modifier = Modifier.size(24.dp)) }
            }
            Spacer(Modifier.height(6.dp))
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun rememberScrollState() = androidx.compose.foundation.rememberScrollState()
