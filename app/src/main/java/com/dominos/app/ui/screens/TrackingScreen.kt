package com.dominos.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import kotlin.math.PI
import kotlin.math.sin

data class TrkStage(val name: String, val icon: @Composable () -> Unit, val complete: Boolean, val active: Boolean)

@Composable
fun TrackingScreen(orderId: String, onBack: () -> Unit, currentStage: Int = 0, isLoadingTracking: Boolean = false) {
    val scheme = MaterialTheme.colorScheme; val stages = remember { listOf(TrkStage("Placed", { Icon(Icons.Default.Receipt, null) }, false, false), TrkStage("Preparing", { Icon(Icons.Default.Restaurant, null) }, false, false), TrkStage("Baking", { Icon(Icons.Default.LocalFireDepartment, null) }, false, false), TrkStage("Quality Check", { Icon(@Suppress("DEPRECATION") Icons.Default.FactCheck, null, tint = scheme.onPrimary) }, false, false), TrkStage("Delivery", { Icon(Icons.Default.DirectionsCar, null) }, false, false), TrkStage("Delivered", { Icon(Icons.Default.CheckCircle, null) }, false, false)) }
    val pC = scheme.primary; val pOC = scheme.onPrimary; val sV = scheme.surfaceVariant; val oS = scheme.onSurface; val oSV = scheme.onSurfaceVariant
    BooScreenScaffold(title = "Track Order", onBack = onBack) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Card(shape = MaterialTheme.shapes.extraLarge, elevation = CardDefaults.cardElevation(4.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
                Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (isLoadingTracking) { CircularProgressIndicator(Modifier.size(32.dp), strokeWidth = 3.dp); Spacer(Modifier.height(16.dp)) }
                    Text("Order #$orderId", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(24.dp))
                    WavyProgress((currentStage + 1).toFloat() / stages.size, Modifier.fillMaxWidth().height(20.dp))
                    Spacer(Modifier.height(24.dp))
                    stages.forEachIndexed { i, st ->
                        val complete = i <= currentStage; val active = i == currentStage
                        val ps by animateFloatAsState(if (active) 1.15f else 1f, if (active) infiniteRepeatable(tween(800, easing = FastOutSlowInEasing), RepeatMode.Reverse) else tween(300), label = "p")
                        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(Modifier.size(44.dp).scale(ps), shape = CircleShape, color = when { active -> pC; complete -> pC.copy(alpha = 0.6f); else -> sV }, tonalElevation = if (active) 4.dp else 0.dp) {
                                Box(contentAlignment = Alignment.Center) { if (complete) Icon(Icons.Default.Check, null, tint = pOC, modifier = Modifier.size(22.dp)); else Text("${i + 1}", color = if (active) pOC else oSV, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
                            }
                            Spacer(Modifier.width(16.dp)); Text(st.name, style = MaterialTheme.typography.bodyLarge, fontWeight = if (active) FontWeight.Bold else FontWeight.Normal, color = if (active) oS else oSV)
                        }
                    }
                }
            }
            if (currentStage >= stages.size - 1) {
                Spacer(Modifier.height(24.dp))
                Card(shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) { Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.onTertiaryContainer, modifier = Modifier.size(28.dp)); Spacer(Modifier.width(12.dp)); Text("Delivered!", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer) } }
            }
        }
    }
}

@Composable
fun WavyProgress(progress: Float, modifier: Modifier = Modifier) {
    val sV = MaterialTheme.colorScheme.surfaceVariant; val pri = MaterialTheme.colorScheme.primary
    val ap by animateFloatAsState(progress, tween(1000, easing = FastOutSlowInEasing), label = "p")
    val wo = rememberInfiniteTransition().animateFloat(0f, 1f, infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Restart), label = "w")
    Canvas(modifier) {
        val th = size.height; val cy = center.y
        drawRoundRect(sV, Offset(0f, cy - th / 2), Size(size.width, th), CornerRadius(th / 2, th / 2))
        if (ap > 0f) {
            val iw = size.width * ap; val p = Path().apply {
                moveTo(0f, cy - th * 0.3f); var x = 0f; val apl = th * 0.25f
                while (x <= iw) { val y = cy + sin((x / 40f + wo.value * 2 * PI.toFloat())) * apl; if (x == 0f) moveTo(x, y) else lineTo(x, y); x += 1f }
                lineTo(iw, cy + th / 2); lineTo(0f, cy + th / 2); close()
            }
            drawPath(p, pri); drawCircle(pri, th / 2, Offset(iw, cy))
        }
    }
}
