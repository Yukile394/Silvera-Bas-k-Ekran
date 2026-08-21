package com.silvera.basikekran.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.silvera.basikekran.ui.theme.BackgroundCard
import com.silvera.basikekran.ui.theme.BorderPurple
import com.silvera.basikekran.ui.theme.NeonPurple

@Composable
fun GlowCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    borderColor: Color = BorderPurple,
    borderWidth: Dp = 1.dp,
    glowColor: Color = NeonPurple,
    glowElevation: Dp = 8.dp,
    backgroundColor: Color = BackgroundCard,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .shadow(
                elevation = glowElevation,
                shape = shape,
                ambientColor = glowColor.copy(alpha = 0.3f),
                spotColor = glowColor.copy(alpha = 0.5f)
            )
            .clip(shape)
            .background(backgroundColor)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = shape
            ),
        content = content
    )
}

@Composable
fun GradientGlowCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    borderColor: Color = BorderPurple,
    glowColor: Color = NeonPurple,
    backgroundBrush: Brush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A0835),
            Color(0xFF0D0420)
        )
    ),
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .shadow(
                elevation = 12.dp,
                shape = shape,
                ambientColor = glowColor.copy(alpha = 0.4f),
                spotColor = glowColor.copy(alpha = 0.6f)
            )
            .clip(shape)
            .background(brush = backgroundBrush)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = shape
            ),
        content = content
    )
}
