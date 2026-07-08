package com.dominos.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dominos.app.ui.components.BooScreenScaffold

data class TrackingStage(val name: String, val icon: @Composable () -> Unit, val isComplete: Boolean, val isActive: Boolean)

@Composable
fun TrackingScreen(orderId: String, onBack: () -> Unit, currentStage: Int = 0, isLoadingTracking: Boolean = false) {
    val stages = remember {
        listOf(
            TrackingStage("Order Placed", { Icon(Icons.Default.Receipt, null) }, false, false),
            TrackingStage("Preparing", { Icon(Icons.Default.Restaurant, null) }, false, false),
            TrackingStage("Baking", { Icon(Icons.Default.LocalFireDepartment, null) }, false, false),
            TrackingStage("Quality Check", { Icon(@Suppress("DEPRECATION") Icons.Default.FactCheck, null) }, false, false),
            TrackingStage("Out for Delivery", { Icon(Icons.Default.DirectionsCar, null) }, false, false),
            TrackingStage("Delivered", { Icon(Icons.Default.CheckCircle, null) }, false, false),
        )
    }

    BooScreenScaffold(title = "Track Order", onBack = onBack) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.extraLarge, elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
                Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (isLoadingTracking) { CircularProgressIndicator(Modifier.size(32.dp), color = MaterialTheme.colorScheme.primary, strokeWidth = 3.dp); Spacer(Modifier.height(16.dp)) }
                    Text("Order #$orderId", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(24.dp))
                    WavyProgressBar(progress = (currentStage + 1).toFloat() / stages.size, modifier = Modifier.fillMaxWidth().height(20.dp))
                    Spacer(Modifier.height(24.dp))
                    stages.forEachIndexed { index, stage ->
                        val isCompleted = index <= currentStage; val isActiveStage = index == currentStage
                        val pulseScale by animateFloatAsState(if (isActiveStage) 1.15f else 1f, if (isActiveStage) infiniteRepeatable(tween(800, easing = FastOutSlowInEasing), RepeatMode.Reverse) else tween(300), label = "pulse")
                        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(Modifier.size(44.dp).scale(pulseScale), shape = CircleShape, color = when { isActiveStage -> MaterialTheme.colorScheme.primary; isCompleted -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f); else -> MaterialTheme.colorScheme.surfaceVariant }, tonalElevation = if (isActiveStage) 4.dp else 0.dp) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { if (isCompleted) Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(22.dp)) else Text("${index + 1}", color = if (isActiveStage) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
                            }
                            Spacer(Modifier.width(16.dp)); Text(stage.name, style = MaterialTheme.typography.bodyLarge, fontWeight = if (isActiveStage) FontWeight.Bold else FontWeight.Normal, color = if (isActiveStage) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            if (currentStage >= stages.size - 1) {
                Spacer(Modifier.height(24.dp))
                Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
                    Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.onTertiaryContainer, modifier = Modifier.size(28.dp)); Spacer(Modifier.width(12.dp)); Text("Your order has been delivered!", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer) }
                }
            }
        }
    }
}

@Composable
private fun WavyProgressBar(progress: Float, modifier: Modifier = Modifier) {
    val trackColor = MaterialTheme.colorScheme.surfaceVariant; val indicatorColor = MaterialTheme.colorScheme.primary
    val animatedProgress by animateFloatAsState(progress, tween(1000, easing = FastOutSlowInEasing), label = "progress")
    val waveOffset = rememberInfiniteTransition().animateFloat(0f, 1f, infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Restart), label = "wave")
    Canvas(modifier) {
        val th = size.height; val cy = center.y
        drawRoundRect(trackColor, Offset(0f, cy - th / 2), Size(size.width, th), CornerRadius(th / 2, th / 2))
        if (animatedProgress > 0f) {
            val iw = size.width * animatedProgress; val path = Path().apply { moveTo(0f, cy - th * 0.3f); var x = 0f; val a = th * 0.25f; val w = 40f
                while (x <= iw) { val y = cy + kotlin.math.sin((x / w + waveOffset.value * 2 * kotlin.math.PI.toFloat())) * a; if (x == 0f) moveTo(x, y) else lineTo(x, y); x += 1f }
                lineTo(iw, cy + th / 2); lineTo(0f, cy + th / 2); close() }
            drawPath(path, indicatorColor); drawCircle(indicatorColor, th / 2, Offset(iw, cy))
        }
    }
}
