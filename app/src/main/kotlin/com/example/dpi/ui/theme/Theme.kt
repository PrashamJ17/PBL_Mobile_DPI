package com.example.dpi.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Light theme colors
private val LightPrimary = Color(0xFF0066FF)
private val LightOnPrimary = Color.White
private val LightSecondary = Color(0xFF03DAC6)
private val LightBackground = Color(0xFFF5F5F5)
private val LightSurface = Color.White
private val LightError = Color(0xFFB00020)

// Dark theme colors
private val DarkPrimary = Color(0xFF4D94FF)
private val DarkOnPrimary = Color.Black
private val DarkSecondary = Color(0xFF03DAC6)
private val DarkBackground = Color(0xFF121212)
private val DarkSurface = Color(0xFF1E1E1E)
private val DarkError = Color(0xFFCF6679)

// Threat severity colors
val CriticalColor = Color(0xFFFF0000)  // Red
val HighColor = Color(0xFFFF6600)      // Orange
val MediumColor = Color(0xFFFFCC00)    // Yellow
val LowColor = Color(0xFF0066FF)       // Blue

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    secondary = LightSecondary,
    background = LightBackground,
    surface = LightSurface,
    error = LightError
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    secondary = DarkSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    error = DarkError
)

@Composable
fun MobileDpiHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}

/**
 * Get color for threat severity
 */
fun getThreatColor(severity: com.example.dpi.models.ThreatSeverity): Color {
    return when (severity) {
        com.example.dpi.models.ThreatSeverity.CRITICAL -> CriticalColor
        com.example.dpi.models.ThreatSeverity.HIGH -> HighColor
        com.example.dpi.models.ThreatSeverity.MEDIUM -> MediumColor
        com.example.dpi.models.ThreatSeverity.LOW -> LowColor
    }
}
