package com.dominos.app.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dominos.app.ui.theme.*

@Composable
fun LoginScreen(
    onGuestContinue: () -> Unit,
    onLogin: (String, String) -> Unit,
    isLoading: Boolean
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().background(DominosRed).animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(80.dp))
        Text("Domino's", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.ExtraBold, color = DominosWhite)
        Text("Pizza Delivery & Carryout", style = MaterialTheme.typography.titleLarge, color = DominosWhite.copy(alpha = 0.8f), modifier = Modifier.padding(top = 4.dp))
        Spacer(Modifier.height(48.dp))

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = DominosWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Sign In", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = DominosDarkGray)
                Spacer(Modifier.height(24.dp))
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DominosRed, cursorColor = DominosRed))
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onLogin(email, password) }),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DominosRed, cursorColor = DominosRed))
                Spacer(Modifier.height(24.dp))
                Button(onClick = { onLogin(email, password) }, modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = DominosRed, contentColor = DominosWhite), enabled = !isLoading
                ) {
                    if (isLoading) CircularProgressIndicator(Modifier.size(24.dp), color = DominosWhite, strokeWidth = 2.dp)
                    else Text("Sign In", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.height(32.dp))
        TextButton(onClick = onGuestContinue, modifier = Modifier.padding(horizontal = 24.dp)) {
            Text("Continue as Guest", style = MaterialTheme.typography.titleMedium, color = DominosWhite, fontWeight = FontWeight.SemiBold)
        }
    }
}
