package com.dominos.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.dominos.app.ui.components.BlooCard
import com.dominos.app.ui.components.BlooGradientBackground
import com.dominos.app.ui.components.BlooMorphButton
import com.dominos.app.ui.components.BlooPebbleCard
import com.dominos.app.ui.components.BlooSectionTitle
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
    val context = LocalContext.current
    val storage = remember { com.dominos.app.data.local.LocalStorage(context) }
    var street by remember { mutableStateOf(storage.getLastStreet()) }
    var zip by remember { mutableStateOf(storage.getLastZipCode()) }
    var isLocating by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            isLocating = true
            try {
                val locationManager = context.getSystemService(android.content.Context.LOCATION_SERVICE) as android.location.LocationManager
                val provider = locationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER) ?: locationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER)
                if (provider != null) {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses: List<Address> = geocoder.getFromLocation(provider.latitude, provider.longitude, 1) ?: emptyList()
                    if (addresses.isNotEmpty()) { zip = addresses[0].postalCode ?: ""; street = "${addresses[0].thoroughfare ?: ""} ${addresses[0].subThoroughfare ?: ""}".trim() }
                }
            } catch (_: Exception) {}
            isLocating = false
        }
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("OpenPizza", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.headlineMedium) },
                navigationIcon = { IconButton(onClick = onAccountClick) { Icon(Icons.Default.AccountCircle, "Account", modifier = Modifier.size(28.dp)) } },
                actions = { BadgedBox(badge = { if (cartItemCount > 0) Badge { Text("$cartItemCount") } }) { IconButton(onClick = onCartClick) { Icon(Icons.Default.ShoppingCart, "Cart", modifier = Modifier.size(28.dp)) } } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface, scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer)
            )
        }
    ) { padding ->
        BlooGradientBackground {
            Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                BlooSectionTitle("Find a Store Near You")
                BlooCard {
                    OutlinedTextField(value = street, onValueChange = { street = it }, label = { Text("Street Address") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = zip, onValueChange = { zip = it }, label = { Text("City or ZIP Code") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary))
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { storage.saveLastStreet(street); storage.saveLastZipCode(zip); onSearch(street, zip) }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = MaterialTheme.shapes.large, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                        Icon(Icons.Default.Search, null, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text("Search", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(8.dp))
                    BlooMorphButton(onClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            isLocating = true; try { val locationManager = context.getSystemService(android.content.Context.LOCATION_SERVICE) as android.location.LocationManager
                                val provider = locationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER) ?: locationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER)
                                if (provider != null) { val gc = Geocoder(context, Locale.getDefault()); val adds: List<Address> = gc.getFromLocation(provider.latitude, provider.longitude, 1) ?: emptyList()
                                    if (adds.isNotEmpty()) { zip = adds[0].postalCode ?: ""; street = "${adds[0].thoroughfare ?: ""} ${adds[0].subThoroughfare ?: ""}".trim() } }
                            } catch (_: Exception) {}; isLocating = false
                        } else locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }, active = !isLocating, modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(vertical = 12.dp)) {
                        if (isLocating) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                        else { Icon(Icons.Default.MyLocation, null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text("Use My Location", fontWeight = FontWeight.Medium) }
                    }
                }

                BlooCard(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalOffer, null, tint = MaterialTheme.colorScheme.onTertiaryContainer, modifier = Modifier.size(32.dp))
                        Spacer(Modifier.width(12.dp))
                        Column { Text("Mix & Match Deal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer); Text("Build your own pizza deal", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)) }
                    }
                }

                if (recentOrderCount > 0) {
                    BlooPebbleCard(icon = Icons.Default.History, iconTint = MaterialTheme.colorScheme.secondary, label = "Recent Orders", subtitle = "$recentOrderCount order${if (recentOrderCount > 1) "s" else ""}", onClick = onAccountClick, badge = "View")
                }
            }
        }
    }
}
