package com.dominos.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dominos.app.ui.components.BooCard
import com.dominos.app.ui.components.BooScreenScaffold
import com.dominos.app.ui.components.BooSectionHeader
import com.dominos.app.viewmodel.AccountUiState

@Composable
fun AccountScreen(
    state: AccountUiState,
    onUpdateField: (String, String) -> Unit,
    onSave: () -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit,
    onNavigateToOrderHistory: () -> Unit = {},
    onNavigateToFavorites: () -> Unit = {},
    onToggleDarkMode: () -> Unit = {},
    orderCount: Int = 0,
    favoritesCount: Int = 0
) {
    BooScreenScaffold(title = "Account", onBack = onBack) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BooCard {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        Modifier.size(72.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Person,
                                null,
                                modifier = Modifier.size(36.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Profile", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    if (state.firstName.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(state.firstName, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            BooCard {
                BooSectionHeader("Personal")
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = state.firstName, onValueChange = { onUpdateField("firstName", it) }, label = { Text("First") }, modifier = Modifier.weight(1f), singleLine = true, shape = MaterialTheme.shapes.medium)
                    OutlinedTextField(value = state.lastName, onValueChange = { onUpdateField("lastName", it) }, label = { Text("Last") }, modifier = Modifier.weight(1f), singleLine = true, shape = MaterialTheme.shapes.medium)
                }
                OutlinedTextField(value = state.phone, onValueChange = { onUpdateField("phone", it) }, label = { Text("Phone") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium)
                OutlinedTextField(value = state.email, onValueChange = { onUpdateField("email", it) }, label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium)
            }
            BooCard {
                BooSectionHeader("Address")
                OutlinedTextField(value = state.street, onValueChange = { onUpdateField("street", it) }, label = { Text("Street") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = state.city, onValueChange = { onUpdateField("city", it) }, label = { Text("City") }, modifier = Modifier.weight(1f), singleLine = true, shape = MaterialTheme.shapes.medium)
                    OutlinedTextField(value = state.region, onValueChange = { onUpdateField("region", it) }, label = { Text("State") }, modifier = Modifier.width(80.dp), singleLine = true, shape = MaterialTheme.shapes.medium)
                    OutlinedTextField(value = state.postalCode, onValueChange = { onUpdateField("postalCode", it) }, label = { Text("ZIP") }, modifier = Modifier.width(100.dp), singleLine = true, shape = MaterialTheme.shapes.medium)
                }
            }
            Button(onClick = onSave, modifier = Modifier.fillMaxWidth().height(56.dp), shape = MaterialTheme.shapes.large) {
                Icon(Icons.Default.Save, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Save Profile", fontWeight = FontWeight.Bold)
            }
            FilledTonalButton(onClick = onNavigateToOrderHistory, modifier = Modifier.fillMaxWidth().height(56.dp), shape = MaterialTheme.shapes.large) {
                Icon(Icons.Default.History, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Order History", fontWeight = FontWeight.Bold)
                if (orderCount > 0) { Spacer(Modifier.width(4.dp)); Badge { Text("$orderCount") } }
            }
            FilledTonalButton(onClick = onNavigateToFavorites, modifier = Modifier.fillMaxWidth().height(56.dp), shape = MaterialTheme.shapes.large) {
                Icon(Icons.Default.Favorite, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Favorites", fontWeight = FontWeight.Bold)
                if (favoritesCount > 0) { Spacer(Modifier.width(4.dp)); Badge { Text("$favoritesCount") } }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Dark Mode", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                Switch(checked = state.isDarkMode, onCheckedChange = { onToggleDarkMode() }, colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary, checkedTrackColor = MaterialTheme.colorScheme.primaryContainer))
            }
            OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth().height(56.dp), shape = MaterialTheme.shapes.large, colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                Icon(Icons.AutoMirrored.Filled.Logout, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Sign Out", fontWeight = FontWeight.Bold)
            }
            Text("OpenPizza v1.0", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}
