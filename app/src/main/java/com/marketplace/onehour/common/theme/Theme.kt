package com.marketplace.onehour.common.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = TealBright,
    onPrimary = Color(0xFF00382E),
    primaryContainer = TealDeep,
    onPrimaryContainer = TextPrimaryDark,
    // Selected-state containers (nav pills, chips) intentionally stay in the
    // teal family everywhere — one consistent "this is active" color rather
    // than mixing in Material3's baseline purple defaults, which is what
    // renders if these are left unset.
    secondary = TerracottaLight,
    onSecondary = Color(0xFF4A1B0D),
    secondaryContainer = Color(0xFF2A4A42),
    onSecondaryContainer = TealBright,
    tertiary = StarYellow,
    tertiaryContainer = Color(0xFF4A3A12),
    onTertiaryContainer = StarYellow,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = CardDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = TextSecondaryDark.copy(alpha = 0.4f),
    error = AlertRed,
    onError = Color.White,
    inversePrimary = TealDeep,
    surfaceTint = TealBright
)

private val LightColorScheme = lightColorScheme(
    primary = TealDeep,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCFEEE7),
    onPrimaryContainer = TealDeep,
    secondary = Terracotta,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCFEEE7),
    onSecondaryContainer = TealDeep,
    tertiary = Color(0xFFAD7C15),
    tertiaryContainer = Color(0xFFFBEACD),
    onTertiaryContainer = Color(0xFF5C4712),
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFF0EEE7),
    onSurfaceVariant = TextSecondaryLight,
    outline = TextSecondaryLight.copy(alpha = 0.35f),
    error = AlertRed,
    onError = Color.White,
    inversePrimary = TealBright,
    surfaceTint = TealDeep
)

@Composable
fun OneHourMarketplaceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
