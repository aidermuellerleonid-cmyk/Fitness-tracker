package de.fitapp.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Green = Color(0xFF2E7D32)
private val GreenDark = Color(0xFF1B5E20)
private val Accent = Color(0xFFFFA000)

private val LightColors = lightColorScheme(
    primary = Green,
    onPrimary = Color.White,
    secondary = Accent,
    background = Color(0xFFF7F9F7),
    surface = Color.White,
    onBackground = Color(0xFF1A1C19),
    onSurface = Color(0xFF1A1C19)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF81C995),
    onPrimary = Color(0xFF0B3D14),
    secondary = Accent,
    background = Color(0xFF10130F),
    surface = Color(0xFF1A1D18),
    onBackground = Color(0xFFE2E3DD),
    onSurface = Color(0xFFE2E3DD)
)

@Composable
fun FitAppTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) DarkColors else LightColors
    MaterialTheme(colorScheme = colors, content = content)
}

val BrandGreenDark = GreenDark
