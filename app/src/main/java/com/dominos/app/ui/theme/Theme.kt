package com.dominos.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ExpressiveLightColors = lightColorScheme(
    primary = DominosRed,
    onPrimary = DominosWhite,
    primaryContainer = DominosRedContainer,
    onPrimaryContainer = DominosRedDark,
    secondary = DominosBlue,
    onSecondary = DominosWhite,
    secondaryContainer = DominosBlue.copy(alpha = 0.12f),
    onSecondaryContainer = DominosDarkBlue,
    tertiary = DominosGreen,
    onTertiary = DominosWhite,
    tertiaryContainer = DominosGreen.copy(alpha = 0.12f),
    onTertiaryContainer = DominosGreen.copy(alpha = 0.8f),
    background = DominosCream,
    onBackground = DominosDarkGray,
    surface = DominosWhite,
    onSurface = DominosDarkGray,
    surfaceVariant = DominosGray,
    onSurfaceVariant = DominosMediumGray,
    surfaceBright = DominosWhite,
    surfaceDim = DominosGray,
    surfaceContainer = DominosGray.copy(alpha = 0.5f),
    surfaceContainerHigh = DominosGray,
    surfaceContainerHighest = DominosLightGray,
    surfaceContainerLow = DominosCream,
    surfaceContainerLowest = DominosWhite,
    outline = DominosLightGray,
    outlineVariant = DominosLightGray.copy(alpha = 0.5f),
    error = Color(0xFFBA1A1A),
    onError = DominosWhite,
    errorContainer = Color(0xFFFFDAD6),
    surfaceTint = DominosRed,
    inverseSurface = DominosDarkGray,
    inverseOnSurface = DominosWhite,
    inversePrimary = DominosRedLight
)

private val ExpressiveDarkColors = darkColorScheme(
    primary = DominosRedDarkMode,
    onPrimary = DominosDarkGray,
    primaryContainer = DominosRedContainerDark,
    onPrimaryContainer = DominosRedLight,
    secondary = DominosBlue.copy(alpha = 0.8f),
    onSecondary = DominosWhite,
    secondaryContainer = DominosDarkBlue,
    onSecondaryContainer = DominosBlue.copy(alpha = 0.6f),
    tertiary = DominosGreen,
    onTertiary = DominosWhite,
    tertiaryContainer = DominosGreen.copy(alpha = 0.15f),
    onTertiaryContainer = DominosGreen.copy(alpha = 0.8f),
    background = DarkSurfaceDim,
    onBackground = DominosWhite,
    surface = DarkSurface,
    onSurface = DominosWhite,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DominosLightGray,
    surfaceBright = DarkSurfaceBright,
    surfaceDim = DarkSurfaceDim,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerHigh = Color(0xFF4A4A4A),
    surfaceContainerHighest = Color(0xFF5A5A5A),
    surfaceContainerLow = DarkSurfaceVariant,
    surfaceContainerLowest = DarkSurface,
    outline = DominosMediumGray,
    outlineVariant = Color(0xFF404040),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    surfaceTint = DominosRedDarkMode,
    inverseSurface = DominosWhite,
    inverseOnSurface = DominosDarkGray,
    inversePrimary = DominosRed
)

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

val AppTypography = Typography(
    displayLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 57.sp, lineHeight = 64.sp, letterSpacing = (-0.25).sp),
    displayMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 45.sp, lineHeight = 52.sp),
    displaySmall = TextStyle(fontWeight = FontWeight.Bold, fontSize = 36.sp, lineHeight = 44.sp),
    headlineLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 40.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 28.sp, lineHeight = 36.sp),
    headlineSmall = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 32.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.15.sp),
    titleSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.5.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.25.sp),
    bodySmall = TextStyle(fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.4.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp)
)

@Composable
fun DominosTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) ExpressiveDarkColors else ExpressiveLightColors
    MaterialTheme(colorScheme = colorScheme, typography = AppTypography, shapes = AppShapes, content = content)
}
