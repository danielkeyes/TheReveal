package dev.danielkeyes.thereveal.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color.Companion.White

private val LightColorPalette = lightColors(
    primary = LightBlue400,
    primaryVariant = LightBlue400Light,
    secondary = LightPink300,
    secondaryVariant = LightPink300Light,
    background = White,
    surface = White,
    onPrimary = Black,
    onSecondary = Black,
    onBackground = Black,
    onSurface = Black,
)

private val DarkColorPalette = darkColors(
    primary = LightBlue400Dark,
    primaryVariant = LightBlue400,
    secondary = LightPink300Dark,
    secondaryVariant = LightPink300,
    background = Black,
    surface = Black,
    onPrimary = White,
    onSecondary = White,
    onBackground = White,
    onSurface = White,
)

@Composable
fun TheRevealTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    // TODO have this toggleable through menu
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
