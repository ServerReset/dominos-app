package com.dominos.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = DominosRed,
    onPrimary = DominosWhite,
    primaryContainer = DominosRed.copy(alpha = 0.12f),
    secondary = DominosBlue,
    onSecondary = DominosWhite,
    tertiary = DominosGreen,
    background = DominosWhite,
    surface = DominosWhite,
    surfaceVariant = DominosGray,
    onBackground = DominosDarkGray,
    onSurface = DominosDarkGray,
    outline = DominosLightGray
)

@Composable
fun DominosTheme(content: @Composable () -> Unit) {
    val colorScheme = LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = DominosRed.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
