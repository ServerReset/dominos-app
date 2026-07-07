package com.dominos.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance

@Composable
fun expressiveButtonColor(): Color {
    val scheme = MaterialTheme.colorScheme
    val dark = scheme.surface.luminance() < 0.5f
    return if (dark) lerp(scheme.surfaceContainerHighest, scheme.onSurface, 0.18f)
    else lerp(scheme.surfaceContainerHighest, scheme.onSurface, 0.20f)
}
