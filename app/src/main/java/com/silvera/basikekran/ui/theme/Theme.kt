package com.silvera.basikekran.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonPurple,
    onPrimary = Color.White,
    primaryContainer = PurpleDim,
    onPrimaryContainer = Color.White,
    secondary = PurpleAccent,
    onSecondary = Color.White,
    secondaryContainer = BackgroundCard,
    onSecondaryContainer = TextSecondary,
    background = BackgroundDeep,
    onBackground = TextPrimary,
    surface = BackgroundCard,
    onSurface = TextPrimary,
    surfaceVariant = BackgroundCardLight,
    onSurfaceVariant = TextSecondary,
    outline = BorderPurple,
    error = Color(0xFFCF6679),
    onError = Color.White
)

@Composable
fun SilveraBasikEkranTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
