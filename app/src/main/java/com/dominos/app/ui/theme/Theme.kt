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
    onPrimaryContainer = Color(0xFF410003),
    secondary = DominosBlue,
    onSecondary = DominosWhite,
    secondaryContainer = Color(0xFFCFE5FF),
    onSecondaryContainer = Color(0xFF001D34),
    tertiary = DominosGreen,
    onTertiary = DominosWhite,
    tertiaryContainer = Color(0xFFCCF0C3),
    onTertiaryContainer = Color(0xFF002204),
    error = Color(0xFFBA1A1A),
    onError = DominosWhite,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = DominosCream,
    onBackground = Color(0xFF1D1B16),
    surface = DominosCream,
    onSurface = Color(0xFF1D1B16),
    surfaceVariant = Color(0xFFF4DDDB),
    onSurfaceVariant = Color(0xFF524341),
    surfaceBright = DominosCream,
    surfaceDim = Color(0xFFE0D7CC),
    surfaceContainer = Color(0xFFF1EDE4),
    surfaceContainerHigh = Color(0xFFEAE4DB),
    surfaceContainerHighest = Color(0xFFE4DED5),
    surfaceContainerLow = Color(0xFFF7F3EA),
    surfaceContainerLowest = DominosWhite,
    outline = Color(0xFF857370),
    outlineVariant = Color(0xFFD8C2BD),
    inverseSurface = Color(0xFF33302B),
    inverseOnSurface = Color(0xFFF7EFE2),
    inversePrimary = Color(0xFFFFB3AB),
    surfaceTint = DominosRed
)

private val ExpressiveDarkColors = darkColorScheme(
    primary = DominosRedDarkMode,
    onPrimary = Color(0xFF680003),
    primaryContainer = DominosRedContainerDark,
    onPrimaryContainer = Color(0xFFFFDAD5),
    secondary = Color(0xFF92CCFF),
    onSecondary = Color(0xFF003352),
    secondaryContainer = Color(0xFF004A74),
    onSecondaryContainer = Color(0xFFCFE5FF),
    tertiary = Color(0xFFA5D99D),
    onTertiary = Color(0xFF023907),
    tertiaryContainer = Color(0xFF1C5118),
    onTertiaryContainer = Color(0xFFCCF0C3),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = DarkSurfaceDim,
    onBackground = Color(0xFFE8E2D8),
    surface = DarkSurfaceDim,
    onSurface = Color(0xFFE8E2D8),
    surfaceVariant = Color(0xFF524341),
    onSurfaceVariant = Color(0xFFD8C2BD),
    surfaceBright = DarkSurfaceBright,
    surfaceDim = DarkSurfaceDim,
    surfaceContainer = Color(0xFF241E1B),
    surfaceContainerHigh = Color(0xFF2E2825),
    surfaceContainerHighest = Color(0xFF39332F),
    surfaceContainerLow = Color(0xFF1D1B16),
    surfaceContainerLowest = Color(0xFF0E0B08),
    outline = Color(0xFFA08D88),
    outlineVariant = Color(0xFF524341),
    inverseSurface = Color(0xFFE8E2D8),
    inverseOnSurface = Color(0xFF33302B),
    inversePrimary = Color(0xFFCC1020),
    surfaceTint = DominosRedDarkMode
)

val M3ExpressiveTypography = Typography(
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

val M3ExpressiveShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun DominosTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) ExpressiveDarkColors else ExpressiveLightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = M3ExpressiveTypography,
        shapes = M3ExpressiveShapes,
        content = content
    )
}
