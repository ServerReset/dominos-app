package com.dominos.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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


data class TrackingStage(val name: String, val icon: @Composable () -> Unit, val isComplete: Boolean, val isActive: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreen(
    orderId: String,
    onBack: () -> Unit,
    currentStage: Int = 0,
    isLoadingTracking: Boolean = false
) {
    val stages = remember {
        listOf(
            TrackingStage("Order Placed", { Icon(Icons.Default.Receipt, contentDescription = null) }, false, false),
            TrackingStage("Preparing", { Icon(Icons.Default.Restaurant, contentDescription = null) }, false, false),
            TrackingStage("Baking", { Icon(Icons.Default.LocalFireDepartment, contentDescription = null) }, false, false),
            TrackingStage("Quality Check", { Icon(@Suppress("DEPRECATION") Icons.Default.FactCheck, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary) }, false, false),
            TrackingStage("Out for Delivery", { Icon(Icons.Default.DirectionsCar, contentDescription = null) }, false, false),
            TrackingStage("Delivered", { Icon(Icons.Default.CheckCircle, contentDescription = null) }, false, false)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Track Order", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface, scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer, titleContentColor = MaterialTheme.colorScheme.onSurface, navigationIconContentColor = MaterialTheme.colorScheme.onSurface)
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
                Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (isLoadingTracking) {
                        CircularProgressIndicator(Modifier.size(32.dp), color = MaterialTheme.colorScheme.primary, strokeWidth = 3.dp)
                        Spacer(Modifier.height(16.dp))
                    }
                    Text("Order #$orderId", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(24.dp))

                    WavyProgressBar(progress = (currentStage + 1).toFloat() / stages.size, modifier = Modifier.fillMaxWidth().height(20.dp))
                    Spacer(Modifier.height(24.dp))

                    stages.forEachIndexed { index, stage ->
                        val isCompleted = index <= currentStage
                        val isActiveStage = index == currentStage
                        val pulseScale by animateFloatAsState(targetValue = if (isActiveStage) 1.15f else 1f,
                            animationSpec = if (isActiveStage) infiniteRepeatable(animation = tween(800, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse) else tween(300), label = "pulse")
                        TrackingStageRow(stage = stage, stageNumber = index + 1, isCompleted = isCompleted, isActive = isActiveStage, isLast = index == stages.size - 1, pulseScale = pulseScale)
                    }
                }
            }

            if (currentStage >= stages.size - 1) {
                Spacer(Modifier.height(24.dp))
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
                    Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(12.dp))
                        Text("Your order has been delivered!", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer)
                    }
                }
            }
        }
    }
}

@Composable
private fun WavyProgressBar(progress: Float, modifier: Modifier = Modifier) {
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val indicatorColor = MaterialTheme.colorScheme.primary
    val animatedProgress by animateFloatAsState(targetValue = progress, animationSpec = tween(1000, easing = FastOutSlowInEasing), label = "progress")
    val waveOffset = rememberInfiniteTransition().animateFloat(initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(animation = tween(1500, easing = LinearEasing), repeatMode = RepeatMode.Restart), label = "wave")
    Canvas(modifier = modifier) {
        val trackHeight = size.height; val trackY = center.y
        drawRoundRect(color = trackColor, topLeft = Offset(0f, trackY - trackHeight / 2), size = Size(size.width, trackHeight), cornerRadius = CornerRadius(trackHeight / 2, trackHeight / 2))
        if (animatedProgress > 0f) {
            val indicatorWidth = size.width * animatedProgress
            val path = Path().apply {
                moveTo(0f, trackY - trackHeight * 0.3f); var x = 0f; val amplitude = trackHeight * 0.25f; val wavelength = 40f
                while (x <= indicatorWidth) {
                    val y = trackY + kotlin.math.sin((x / wavelength + waveOffset.value * 2 * kotlin.math.PI.toFloat())) * amplitude
                    if (x == 0f) moveTo(x, y) else lineTo(x, y); x += 1f
                }
                lineTo(indicatorWidth, trackY + trackHeight / 2); lineTo(0f, trackY + trackHeight / 2); close()
            }
            drawPath(path = path, color = indicatorColor)
            drawCircle(color = indicatorColor, radius = trackHeight / 2, center = Offset(indicatorWidth, trackY))
        }
    }
}

@Composable
private fun TrackingStageRow(stage: TrackingStage, stageNumber: Int, isCompleted: Boolean, isActive: Boolean, isLast: Boolean, pulseScale: Float) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(Modifier.size(44.dp).scale(pulseScale), shape = CircleShape, color = when { isActive -> MaterialTheme.colorScheme.primary; isCompleted -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f); else -> MaterialTheme.colorScheme.surfaceVariant }, tonalElevation = if (isActive) 4.dp else 0.dp) {
            Box(contentAlignment = Alignment.Center) {
                if (isCompleted) Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(22.dp))
                else Text("$stageNumber", color = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
        Spacer(Modifier.width(16.dp))
        Text(stage.name, style = MaterialTheme.typography.bodyLarge, fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal, color = if (isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
