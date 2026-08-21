package com.silvera.basikekran.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.silvera.basikekran.ui.components.AnimatedBackground
import com.silvera.basikekran.ui.theme.*

@Composable
fun AboutScreen() {
    val infiniteTransition = rememberInfiniteTransition(label = "about_anim")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(2500, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "glow"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // App logo / branding
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(120.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Color(0xFF9B3DFF).copy(glowAlpha * 0.5f), Color.Transparent)
                        ),
                        radius = size.minDimension / 1.5f
                    )
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(90.dp)
                        .shadow(20.dp, CircleShape,
                            ambientColor = NeonPurple.copy(glowAlpha * 0.6f),
                            spotColor = NeonPurple.copy(glowAlpha))
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(Color(0xFF5A1FA0), Color(0xFF2D0860))))
                        .border(2.dp, NeonPurpleLight.copy(0.7f), CircleShape)
                ) {
                    Icon(Icons.Default.SportsEsports, contentDescription = null,
                        tint = Color.White, modifier = Modifier.size(46.dp))
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Silvera", color = NeonPurpleLight, fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold, letterSpacing = 4.sp)
                Text("Basık Ekran", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(6.dp))
                Text("Versiyon 1.0.0", color = TextDim, fontSize = 12.sp)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Mobil oyunlarda daha iyi ekran deneyimi",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Features list
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(18.dp), ambientColor = NeonPurple.copy(0.15f))
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.verticalGradient(listOf(Color(0xFF130630), Color(0xFF0A0320))))
                    .border(1.dp, BorderPurple, RoundedCornerShape(18.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Özellikler", color = NeonPurpleLight, fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
                FeatureItem(Icons.Default.AspectRatio, "Hızlı ekran oranı değiştirme (16:9, 4:3, 21:9)")
                FeatureItem(Icons.Default.Tune, "Özel genişlik ve yükseklik ayarları")
                FeatureItem(Icons.Default.Save, "Oyun başına profil kaydetme")
                FeatureItem(Icons.Default.SportsEsports, "Standoff 2 & PUBG Mobile desteği")
                FeatureItem(Icons.Default.Extension, "Modüler oyun sistemi (daha fazlası geliyor)")
                FeatureItem(Icons.Default.Palette, "Neon mor Silvera teması")
            }

            // Technical info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF0A0320).copy(0.8f))
                    .border(1.dp, BorderPurple.copy(0.4f), RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("Teknik Bilgi", color = NeonPurpleLight, fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
                TechRow("Geliştirici", "Silvera Team")
                TechRow("Platform", "Android 7.0+")
                TechRow("Dil", "Kotlin + Jetpack Compose")
                TechRow("Build", "GitHub Actions CI/CD")
                TechRow("Lisans", "Özel")
            }

            // Copyright
            Text(
                "© 2024 Silvera. Tüm hakları saklıdır.",
                color = TextDim,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun FeatureItem(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = NeonPurple.copy(0.8f), modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(10.dp))
        Text(text, color = TextSecondary, fontSize = 13.sp, lineHeight = 18.sp)
    }
}

@Composable
private fun TechRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = TextDim, fontSize = 12.sp)
        Text(value, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
