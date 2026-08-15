package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val StudioColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = Color(0xFF041E28),
    primaryContainer = Color(0xFF0C3847),
    onPrimaryContainer = CyberCyan,
    secondary = CyberBlue,
    onSecondary = Color(0xFF031938),
    secondaryContainer = Color(0xFF143260),
    onSecondaryContainer = Color(0xFFBCE0FD),
    tertiary = NeonPurple,
    onTertiary = Color(0xFF260447),
    tertiaryContainer = Color(0xFF3B156B),
    onTertiaryContainer = Color(0xFFE5D4FF),
    background = ObsidianBg,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = BorderGlow,
    outlineVariant = BorderSubtle,
    error = CrimsonRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to dark mode for cinematic studio feel
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = StudioColorScheme,
        typography = Typography,
        content = content
    )
}
