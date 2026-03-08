/*
 * NegoPlayer - Advanced Android Media Platform
 * Author: Muhammad Umar
 * Project: NegoPlayer
 * File: Theme.kt
 */

package com.negoplayer.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// NegoPlayer Brand Colors
val NegoGreen = Color(0xFF00C853)
val NegoGreenDark = Color(0xFF009624)
val NegoGreenLight = Color(0xFF5EFC82)
val NegoDarkBackground = Color(0xFF0A0A0A)
val NegoDarkSurface = Color(0xFF1A1A1A)
val NegoDarkSecondary = Color(0xFF2D2D2D)
val NegoAccent = Color(0xFF69F0AE)
val NegoError = Color(0xFFCF6679)

private val DarkColorScheme = darkColorScheme(
    primary = NegoGreen,
    onPrimary = Color.Black,
    primaryContainer = NegoGreenDark,
    onPrimaryContainer = NegoGreenLight,
    secondary = NegoAccent,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF1B5E20),
    onSecondaryContainer = NegoGreenLight,
    tertiary = Color(0xFF80CBC4),
    background = NegoDarkBackground,
    onBackground = Color(0xFFE8E8E8),
    surface = NegoDarkSurface,
    onSurface = Color(0xFFE8E8E8),
    surfaceVariant = NegoDarkSecondary,
    onSurfaceVariant = Color(0xFFBDBDBD),
    outline = Color(0xFF424242),
    error = NegoError,
    onError = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = NegoGreenDark,
    onPrimary = Color.White,
    primaryContainer = NegoGreenLight,
    onPrimaryContainer = Color(0xFF002106),
    secondary = Color(0xFF52796F),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB0CCC4),
    onSecondaryContainer = Color(0xFF0F2420),
    background = Color(0xFFF8F9FA),
    onBackground = Color(0xFF1A1A1A),
    surface = Color.White,
    onSurface = Color(0xFF1A1A1A),
    error = Color(0xFFB00020)
)

@Composable
fun NegoPlayerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NegoTypography,
        content = content
    )
}
