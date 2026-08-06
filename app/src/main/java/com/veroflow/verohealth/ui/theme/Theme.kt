package com.veroflow.verohealth.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = TealPrimary,
    onPrimary = Color.White,
    primaryContainer = BluePrimaryContainer,
    background = LightBackground,
    surface = LightSurface,
    error = HealthRed
)

private val DarkColors = darkColorScheme(
    primary = TealPrimaryDark,
    onPrimary = Color.Black,
    background = DarkBackground,
    surface = DarkSurface,
    error = HealthRed
)

/**
 * ThemeMode mirrors the Settings screen's Theme option (Light / Dark / System Default).
 * Kept as a simple enum here; Settings (Screen 33) will read/write it via Session.
 */
enum class ThemeMode { LIGHT, DARK, SYSTEM }

@Composable
fun VeroHealthTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val useDark = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    MaterialTheme(
        colorScheme = if (useDark) DarkColors else LightColors,
        typography = VeroHealthTypography,
        content = content
    )
}
