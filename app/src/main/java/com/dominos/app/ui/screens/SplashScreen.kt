package com.dominos.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dominos.app.ui.theme.DominosRed
import com.dominos.app.ui.theme.DominosWhite
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(1000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )
    val fadeAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "fade"
    )

    LaunchedEffect(Unit) {
        delay(2000)
        onSplashFinished()
    }

    Box(Modifier.fillMaxSize().background(DominosRed), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Domino's", modifier = Modifier.scale(pulseScale).alpha(fadeAlpha),
                style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.ExtraBold, color = DominosWhite)
            Spacer(Modifier.height(8.dp))
            Text("Pizza Delivery & Carryout", style = MaterialTheme.typography.titleMedium, color = DominosWhite.copy(alpha = 0.8f))
            Spacer(Modifier.height(48.dp))
            Text("Loading...", style = MaterialTheme.typography.bodyMedium, color = DominosWhite.copy(alpha = 0.6f))
        }
    }
}
