package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldBright,
    onPrimary = ObsidianBlack,
    primaryContainer = ForestGreen,
    onPrimaryContainer = MintAccent,
    secondary = EmeraldGlow,
    onSecondary = ObsidianBlack,
    secondaryContainer = EmeraldDark,
    onSecondaryContainer = TextSecondary,
    tertiary = MintAccent,
    onTertiary = ObsidianBlack,
    background = ObsidianBlack,
    onBackground = TextPrimary,
    surface = DarkEmeraldBlack,
    onSurface = TextPrimary,
    surfaceVariant = DeepEmeraldCard,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorder,
    outlineVariant = GlassBorderSubtle
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to deep black & glassy green theme
    dynamicColor: Boolean = false, // Keep bespoke styling
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
