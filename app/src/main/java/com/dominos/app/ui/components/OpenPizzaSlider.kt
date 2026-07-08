package com.dominos.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dominos.app.ui.theme.SoftDamping
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun OpenPizzaSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    steps: Int = 0,
    accent: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.surfaceVariant,
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    var widthPx by remember { mutableFloatStateOf(0f) }
    val anim = remember { Animatable(value) }
    var dragging by remember { mutableStateOf(false) }
    var settled by remember { mutableStateOf(true) }

    LaunchedEffect(value) { if (!dragging && anim.value != value) anim.snapTo(value) }

    Box(
        Modifier
            .fillMaxWidth()
            .height(44.dp)
            .onSizeChanged { widthPx = it.width.toFloat() }
            .pointerInput(valueRange, steps) {
                val padPx = with(density) { 14.dp.toPx() }
                val travelPx = (widthPx - 2 * padPx).coerceAtLeast(1f)
                fun posForX(x: Float): Float { val frac = (x - padPx) / travelPx; return valueRange.start + frac * (valueRange.endInclusive - valueRange.start) }
                fun trackTo(x: Float) { onValueChange(snapToStep(posForX(x), valueRange, steps)) }
                fun settleTo(v: Float) { onValueChange(v) }
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val slop = viewConfiguration.touchSlop
                    var claimed = false
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        if (!change.pressed) { break }
                        if (!claimed) {
                            val dx = abs(change.position.x - down.position.x)
                            if (dx > slop) { claimed = true; dragging = true; change.consume(); trackTo(change.position.x) }
                        } else if (change.positionChanged()) { trackTo(change.position.x); change.consume() }
                    }
                    if (claimed) { dragging = false; settleTo(snapToStep(anim.value, valueRange, steps)) }
                }
            }
            .progressSemantics(value, valueRange, steps),
    ) {
        Canvas(Modifier.fillMaxWidth().height(44.dp)) {
            val span = (valueRange.endInclusive - valueRange.start).coerceAtLeast(0.001f)
            val frac = (anim.value - valueRange.start) / span
            val tw = 6.dp.toPx(); val th = trackThickness.toPx(); val gapPx = 6.dp.toPx()
            val padPx = 14.dp.toPx(); val travel = (size.width - 2 * padPx).coerceAtLeast(0f)
            val thumbX = padPx + travel * frac; val cy = size.height / 2f; val top = cy - th / 2f
            val cut = tw / 2f + gapPx; val radius = CornerRadius(th / 2f)
            val inStart = (thumbX + cut).coerceAtMost(size.width)
            if (inStart < size.width) drawRoundRect(inactiveColor, topLeft = Offset(inStart, top), size = Size(size.width - inStart, th), cornerRadius = radius)
            val acEnd = (thumbX - cut).coerceAtLeast(0f)
            if (acEnd > 0f) drawRoundRect(accent, topLeft = Offset(0f, top), size = Size(acEnd, th), cornerRadius = radius)
            drawRoundRect(accent, topLeft = Offset(thumbX - tw / 2f, 0f), size = Size(tw, size.height), cornerRadius = CornerRadius(tw / 2f))
        }
    }
}

private val trackThickness = 14.dp

fun snapToStep(v: Float, range: ClosedFloatingPointRange<Float>, steps: Int): Float {
    if (steps <= 0) return v.coerceIn(range.start, range.endInclusive)
    val inc = (range.endInclusive - range.start) / (steps + 1)
    return (range.start + (v - range.start).div(inc).roundToInt() * inc).coerceIn(range.start, range.endInclusive)
}
