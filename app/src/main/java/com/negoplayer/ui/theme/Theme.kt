package com.negoplayer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// NegoPlayer color palette
val NegoBlue = Color(0xFF1976D2)
val NegoBlueLight = Color(0xFF42A5F5)
val NegoBlueDark = Color(0xFF0D47A1)
val NegoAccent = Color(0xFF00BCD4)
val NegoBackground = Color(0xFF121212)
val NegoSurface = Color(0xFF1E1E1E)

private val DarkColorScheme = darkColorScheme(
    primary = NegoBlueLight,
    onPrimary = Color.Black,
    primaryContainer = NegoBlueDark,
    onPrimaryContainer = Color.White,
    secondary = NegoAccent,
    onSecondary = Color.Black,
    background = NegoBackground,
    onBackground = Color.White,
    surface = NegoSurface,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2D2D2D),
    onSurfaceVariant = Color(0xFFCCCCCC)
)

private val LightColorScheme = lightColorScheme(
    primary = NegoBlue,
    onPrimary = Color.White,
    primaryContainer = NegoBlueLight,
    onPrimaryContainer = Color(0xFF001A33),
    secondary = Color(0xFF0097A7),
    onSecondary = Color.White,
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF111111),
    surface = Color.White,
    onSurface = Color(0xFF111111),
    surfaceVariant = Color(0xFFEEEEEE),
    onSurfaceVariant = Color(0xFF555555)
)

@Composable
fun NegoPlayerTheme(
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
