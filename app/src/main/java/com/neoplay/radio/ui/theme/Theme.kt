package com.neoplay.radio.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CyanPrimary,
    secondary = CyanSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = DarkBackground,
    onBackground = TextWhite,
    onSurface = TextWhite
)

private val LightColorScheme = lightColorScheme(
    primary = CyanPrimary,
    secondary = CyanSecondary,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = DarkBackground,
    onBackground = DarkBackground,
    onSurface = DarkBackground
)

@Composable
fun NeoRadioTheme(
    themeSetting: String = "dark",
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeSetting) {
        "light" -> false
        "system" -> isSystemInDarkTheme()
        else -> true
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
