package com.silvera.basikekran.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.silvera.basikekran.ui.theme.BackgroundDeep
import com.silvera.basikekran.ui.theme.NeonPurple
import com.silvera.basikekran.ui.theme.NeonPurpleGlow
import com.silvera.basikekran.ui.theme.NeonPurpleDark

@Composable
fun AnimatedBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "bg_anim")

    val alpha1 by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha1"
    )

    val alpha2 by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.40f,
        animationSpec = infiniteRepeatable(
            animation = tween(5500, easing = EaseInOutSine, delayMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha2"
    )

    val alpha3 by infiniteTransition.animateFloat(
        initialValue = 0.10f,
        targetValue = 0.30f,
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = EaseInOutSine, delayMillis = 2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha3"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDeep)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Top-left purple glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        NeonPurpleGlow.copy(alpha = alpha1 * 0.6f),
                        NeonPurple.copy(alpha = alpha1 * 0.2f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.15f, size.height * 0.12f),
                    radius = size.width * 0.55f
                ),
                radius = size.width * 0.55f,
                center = Offset(size.width * 0.15f, size.height * 0.12f)
            )

            // Bottom-right purple glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        NeonPurpleDark.copy(alpha = alpha2 * 0.5f),
                        NeonPurple.copy(alpha = alpha2 * 0.15f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.85f, size.height * 0.88f),
                    radius = size.width * 0.5f
                ),
                radius = size.width * 0.5f,
                center = Offset(size.width * 0.85f, size.height * 0.88f)
            )

            // Center subtle glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        NeonPurple.copy(alpha = alpha3 * 0.3f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.5f, size.height * 0.5f),
                    radius = size.width * 0.7f
                ),
                radius = size.width * 0.7f,
                center = Offset(size.width * 0.5f, size.height * 0.5f)
            )

            // Top-right accent
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF4A00C8).copy(alpha = alpha1 * 0.4f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.9f, size.height * 0.05f),
                    radius = size.width * 0.35f
                ),
                radius = size.width * 0.35f,
                center = Offset(size.width * 0.9f, size.height * 0.05f)
            )
        }
    }
}
