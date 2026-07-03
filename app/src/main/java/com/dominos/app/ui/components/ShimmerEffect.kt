package com.dominos.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerEffect(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = -1000f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Restart),
        label = "shimmer_translate"
    )
    val brush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.surfaceVariant
        ),
        start = Offset(translateAnim, 0f), end = Offset(translateAnim + 200f, 0f)
    )
    Box(modifier = modifier.background(brush))
}

@Composable
fun ShimmerProductCard() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ShimmerEffect(modifier = Modifier.fillMaxWidth().height(24.dp).clip(RoundedCornerShape(12.dp)))
        ShimmerEffect(modifier = Modifier.fillMaxWidth(0.6f).height(16.dp).clip(RoundedCornerShape(8.dp)))
        ShimmerEffect(modifier = Modifier.fillMaxWidth(0.4f).height(16.dp).clip(RoundedCornerShape(8.dp)))
    }
}

@Composable
fun ShimmerStoreCard() {
    ShimmerEffect(modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(20.dp)))
}
