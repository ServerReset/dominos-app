package com.dominos.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
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
import com.dominos.app.ui.components.BlooMorphButton
import com.dominos.app.ui.components.BooCard
import com.dominos.app.ui.components.BooScreenScaffold
import com.dominos.app.ui.components.BooSectionHeader
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

    BooScreenScaffold(title = "OpenPizza", onBack = null) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            BooCard {
                OutlinedTextField(
                    value = street,
                    onValueChange = { street = it },
                    label = { Text("Street") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = zip,
                    onValueChange = { zip = it },
                    label = { Text("ZIP") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        storage.saveLastStreet(street)
                        storage.saveLastZipCode(zip)
                        onSearch(street, zip)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Icon(Icons.Default.Search, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Search", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
                        BlooMorphButton(
                            onClick = {
                                if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
                                } else { permLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }
                            },
                    active = !locating,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    if (locating) {
                        CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.MyLocation, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Use My Location")
                    }
                }
            }
            BooCard(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocalOffer,
                        null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "Mix & Match Deal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            "Build your own deal",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            if (recentOrderCount > 0) {
                Button(
                    onClick = onAccountClick,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.filledTonalButtonColors()
                ) {
                    Icon(Icons.Default.History, null)
                    Spacer(Modifier.width(8.dp))
                    Text("$recentOrderCount recent order${if (recentOrderCount > 1) "s" else ""}")
                }
            }
        }
    }
}
