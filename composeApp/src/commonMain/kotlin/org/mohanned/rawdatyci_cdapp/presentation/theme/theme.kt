package org.mohanned.rawdatyci_cdapp.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ══════════════════════════════════════
//  LIGHT COLOR SCHEME
// ══════════════════════════════════════

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = White,
    primaryContainer = BlueLight,
    onPrimaryContainer = BlueDark,
    secondary = MintPrimary,
    onSecondary = White,
    secondaryContainer = MintLight,
    onSecondaryContainer = MintDark,
    background = AppBg,
    surface = SurfaceLight,
    onBackground = Gray900,
    onSurface = Gray800,
    surfaceVariant = Gray100,
    onSurfaceVariant = Gray600,
    outline = Gray300,
    error = ColorError,
    onError = White,
)

// ══════════════════════════════════════
//  DARK COLOR SCHEME
// ══════════════════════════════════════

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6BA6D1), // Lighter blue for dark mode
    onPrimary = Black,
    primaryContainer = BlueDark,
    secondary = Color(0xFF6ECF9E), // Lighter mint for dark mode
    onSecondary = Black,
    background = Color(0xFF0F172A), // Slate 900
    surface = Color(0xFF1E293B), // Slate 800
    onBackground = Gray100,
    onSurface = Gray100,
    error = Color(0xFFFB7185),
    onError = Black,
)

// ══════════════════════════════════════
//  SHAPES (Consistent & Sleek)
// ══════════════════════════════════════

val AppShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

// ══════════════════════════════════════
//  THEME COMPOSABLE
// ══════════════════════════════════════

// Placeholder for Cairo FontWeight (User should add .ttf files to composeResources/font)
val CairoFontFamily = FontFamily.Default // Should be replaced with actual Cairo font logic

@Composable
fun RawdatyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val typography = Typography(
        displayLarge = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold, fontSize = 48.sp),
        displayMedium = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold, fontSize = 36.sp),
        displaySmall = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold, fontSize = 30.sp),
        headlineLarge = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold, fontSize = 28.sp),
        headlineMedium = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold, fontSize = 24.sp),
        headlineSmall = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
        titleLarge = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
        titleMedium = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Medium, fontSize = 16.sp),
        titleSmall = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp),
        bodyLarge = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp),
        bodyMedium = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp),
        bodySmall = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp),
        labelLarge = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp),
        labelMedium = TextStyle(fontFamily = CairoFontFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp),
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = AppShapes,
        content = content
    )
}