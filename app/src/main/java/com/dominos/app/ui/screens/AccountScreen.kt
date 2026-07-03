package com.dominos.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dominos.app.viewmodel.AccountUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    state: AccountUiState,
    onUpdateField: (String, String) -> Unit,
    onSave: () -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit,
    onNavigateToOrderHistory: () -> Unit = {},
    onNavigateToFavorites: () -> Unit = {},
    onToggleDarkMode: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Account", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface, scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer)
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
                Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(Modifier.size(72.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer, tonalElevation = 2.dp) {
                        Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer) }
                    }
                    Text("Profile", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
            }

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Personal Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = state.firstName, onValueChange = { onUpdateField("firstName", it) }, label = { Text("First Name") }, modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary))
                        OutlinedTextField(value = state.lastName, onValueChange = { onUpdateField("lastName", it) }, label = { Text("Last Name") }, modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary))
                    }
                    OutlinedTextField(value = state.phone, onValueChange = { onUpdateField("phone", it) }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(14.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary))
                    OutlinedTextField(value = state.email, onValueChange = { onUpdateField("email", it) }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(14.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary))
                }
            }

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Address", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    OutlinedTextField(value = state.street, onValueChange = { onUpdateField("street", it) }, label = { Text("Street Address") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = state.city, onValueChange = { onUpdateField("city", it) }, label = { Text("City") }, modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary))
                        OutlinedTextField(value = state.region, onValueChange = { onUpdateField("region", it) }, label = { Text("State") }, modifier = Modifier.width(80.dp), singleLine = true, shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary))
                        OutlinedTextField(value = state.postalCode, onValueChange = { onUpdateField("postalCode", it) }, label = { Text("ZIP") }, modifier = Modifier.width(100.dp), singleLine = true, shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary))
                    }
                }
            }

            Button(onClick = onSave, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text("Save Profile", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(onClick = onNavigateToOrderHistory, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary)) {
                Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text("Order History", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(onClick = onNavigateToFavorites, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary)) {
                Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text("Favorites", fontWeight = FontWeight.Bold)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Dark Mode", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                Switch(checked = state.isDarkMode, onCheckedChange = { onToggleDarkMode() }, colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary, checkedTrackColor = MaterialTheme.colorScheme.primaryContainer))
            }

            OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text("Sign Out", fontWeight = FontWeight.Bold)
            }
        }
    }
}
