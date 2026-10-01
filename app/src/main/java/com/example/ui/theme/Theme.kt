package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = TradeGreen,
    onPrimary = Color.Black,
    primaryContainer = TradeGreenBg,
    onPrimaryContainer = TradeGreenGlow,
    secondary = TradeCyan,
    onSecondary = Color.Black,
    secondaryContainer = TradeCyanBg,
    onSecondaryContainer = TradeCyan,
    tertiary = TradeAmber,
    onTertiary = Color.Black,
    tertiaryContainer = TradeAmberBg,
    onTertiaryContainer = TradeAmber,
    error = TradeRed,
    onError = Color.White,
    errorContainer = TradeRedBg,
    onErrorContainer = TradeRedGlow,
    background = TradingDarkBg,
    onBackground = TextPrimary,
    surface = TradingSurface,
    onSurface = TextPrimary,
    surfaceVariant = TradingSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = TradingBorder,
    outlineVariant = TradingSurfaceHighlight
)

// Trading terminals are ideally dark-themed, but we provide an accessible light scheme as well
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF008947),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE2F9EE),
    onPrimaryContainer = Color(0xFF00391A),
    secondary = Color(0xFF00838F),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F7FA),
    onSecondaryContainer = Color(0xFF00363A),
    tertiary = Color(0xFFE65100),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFF3E0),
    onTertiaryContainer = Color(0xFF5D2000),
    error = Color(0xFFD32F2F),
    onError = Color.White,
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = Color(0xFF6B0000),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFEEF2F6),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek pro dark mode for trading terminal
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
