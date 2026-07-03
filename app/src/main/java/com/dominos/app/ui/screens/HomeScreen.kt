package com.dominos.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    var street by remember { mutableStateOf("") }
    var zip by remember { mutableStateOf("") }
    var isLocating by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val locationPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            isLocating = true
            try {
                val locationManager = context.getSystemService(android.content.Context.LOCATION_SERVICE) as android.location.LocationManager
                val provider = locationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER) ?: locationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER)
                if (provider != null) {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses: List<Address> = geocoder.getFromLocation(provider.latitude, provider.longitude, 1) ?: emptyList()
                    if (addresses.isNotEmpty()) {
                        zip = addresses[0].postalCode ?: ""
                        street = "${addresses[0].thoroughfare ?: ""} ${addresses[0].subThoroughfare ?: ""}".trim()
                    }
                }
            } catch (e: Exception) { /* silently fail */ }
            isLocating = false
        }
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("OpenPizza", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.headlineMedium) },
                navigationIcon = { IconButton(onClick = onAccountClick) { Icon(Icons.Default.AccountCircle, contentDescription = "Account", modifier = Modifier.size(28.dp)) } },
                actions = {
                    BadgedBox(badge = { if (cartItemCount > 0) Badge { Text("$cartItemCount") } }) {
                        IconButton(onClick = onCartClick) { Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", modifier = Modifier.size(28.dp)) }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface, scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer)
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp, vertical = 16.dp)) {
            Text("Find a Store Near You", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
                Column(Modifier.padding(16.dp)) {
                    OutlinedTextField(value = street, onValueChange = { street = it }, label = { Text("Street Address") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary))
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = zip, onValueChange = { zip = it }, label = { Text("City or ZIP Code") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary))
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { onSearch(street, zip) }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = MaterialTheme.shapes.large, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)) {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text("Search", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(8.dp))
                    FilledTonalButton(onClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            isLocating = true
                            try {
                                val locationManager = context.getSystemService(android.content.Context.LOCATION_SERVICE) as android.location.LocationManager
                                val provider = locationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER) ?: locationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER)
                                if (provider != null) {
                                    val geocoder = Geocoder(context, Locale.getDefault())
                                    val addresses: List<Address> = geocoder.getFromLocation(provider.latitude, provider.longitude, 1) ?: emptyList()
                                    if (addresses.isNotEmpty()) {
                                        zip = addresses[0].postalCode ?: ""
                                        street = "${addresses[0].thoroughfare ?: ""} ${addresses[0].subThoroughfare ?: ""}".trim()
                                    }
                                }
                            } catch (e: Exception) { /* silently fail */ }
                            isLocating = false
                        } else {
                            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    }, modifier = Modifier.fillMaxWidth().height(48.dp), shape = MaterialTheme.shapes.large, enabled = !isLocating) {
                        if (isLocating) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                        else { Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text("Use My Location", fontWeight = FontWeight.Medium) }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Row(Modifier.padding(20.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalOffer, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Mix & Match Deal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text("Build your own pizza deal", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                    }
                }
            }

            if (recentOrderCount > 0) {
                Spacer(Modifier.height(16.dp))
                Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
                    Row(Modifier.padding(20.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("$recentOrderCount recent order${if (recentOrderCount > 1) "s" else ""}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("Tap Account to view history", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
