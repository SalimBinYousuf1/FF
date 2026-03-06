package com.vibeplayer.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF9B84FF),
    onPrimary = Color(0xFF1A1040),
    primaryContainer = Color(0xFF3D2B8F),
    onPrimaryContainer = Color(0xFFE0D9FF),
    secondary = Color(0xFFFF84B7),
    onSecondary = Color(0xFF3D001F),
    secondaryContainer = Color(0xFF63003A),
    onSecondaryContainer = Color(0xFFFFD9E6),
    background = Color(0xFF0D0D14),
    onBackground = Color(0xFFE8E8F0),
    surface = Color(0xFF15151F),
    onSurface = Color(0xFFE8E8F0),
    surfaceVariant = Color(0xFF1E1E2E),
    onSurfaceVariant = Color(0xFFBBB9CC),
    surfaceContainer = Color(0xFF1A1A27),
    surfaceContainerHigh = Color(0xFF22222F),
    surfaceContainerHighest = Color(0xFF2A2A38),
    outline = Color(0xFF45455A),
    outlineVariant = Color(0xFF2D2D42),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF5B3FCF),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE8DDFF),
    onPrimaryContainer = Color(0xFF1B0062),
    secondary = Color(0xFFB5175A),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFD9E6),
    onSecondaryContainer = Color(0xFF3D001F),
    background = Color(0xFFFAF8FF),
    onBackground = Color(0xFF1A1A2A),
    surface = Color(0xFFF5F3FF),
    onSurface = Color(0xFF1A1A2A),
    surfaceVariant = Color(0xFFEBE7F5),
    onSurfaceVariant = Color(0xFF4A4560),
)

@Composable
fun VibePlayerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
