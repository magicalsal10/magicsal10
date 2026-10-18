package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = TealPrimary,
    onPrimary = SlateDark,
    primaryContainer = TealDark,
    onPrimaryContainer = SlateTextPrimary,
    secondary = GoldAutophagy,
    onSecondary = SlateDark,
    secondaryContainer = SlateSurfaceVariant,
    onSecondaryContainer = AmberGlow,
    tertiary = SkyBlue,
    onTertiary = SlateDark,
    background = SlateBackground,
    onBackground = SlateTextPrimary,
    surface = SlateSurface,
    onSurface = SlateTextPrimary,
    surfaceVariant = SlateSurfaceVariant,
    onSurfaceVariant = SlateTextSecondary,
    outline = SlateBorder,
    error = RoseError,
    onError = SlateTextPrimary
)

@Composable
fun FastPulseTheme(
    darkTheme: Boolean = true, // Defaults to modern sleek dark theme
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
