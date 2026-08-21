package com.silvera.basikekran.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.silvera.basikekran.ui.components.AnimatedBackground
import com.silvera.basikekran.ui.theme.*

@Composable
fun SettingsScreen() {
    var animationsEnabled by remember { mutableStateOf(true) }
    var hapticEnabled by remember { mutableStateOf(false) }
    var autoLaunchEnabled by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Ayarlar", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)

            // General settings
            SettingsGroup(title = "Genel") {
                SettingsToggleRow(
                    icon = Icons.Default.Animation,
                    title = "Animasyonlar",
                    subtitle = "Akıcı geçiş efektleri",
                    checked = animationsEnabled,
                    onCheckedChange = { animationsEnabled = it }
                )
                HorizontalDivider(color = BorderPurple.copy(0.4f), modifier = Modifier.padding(horizontal = 8.dp))
                SettingsToggleRow(
                    icon = Icons.Default.Vibration,
                    title = "Titreşim",
                    subtitle = "Dokunmatik geri bildirim",
                    checked = hapticEnabled,
                    onCheckedChange = { hapticEnabled = it }
                )
                HorizontalDivider(color = BorderPurple.copy(0.4f), modifier = Modifier.padding(horizontal = 8.dp))
                SettingsToggleRow(
                    icon = Icons.Default.PlayArrow,
                    title = "Otomatik Başlatma",
                    subtitle = "Oyun seçiminde hemen başlat",
                    checked = autoLaunchEnabled,
                    onCheckedChange = { autoLaunchEnabled = it }
                )
            }

            // Screen settings
            SettingsGroup(title = "Ekran") {
                SettingsInfoRow(
                    icon = Icons.Default.ScreenRotation,
                    title = "Ekran Yönü",
                    value = "Dikey"
                )
                HorizontalDivider(color = BorderPurple.copy(0.4f), modifier = Modifier.padding(horizontal = 8.dp))
                SettingsInfoRow(
                    icon = Icons.Default.AspectRatio,
                    title = "Varsayılan Oran",
                    value = "4:3"
                )
            }

            // Support info
            SettingsGroup(title = "Desteklenen Android Sürümleri") {
                Column(modifier = Modifier.padding(12.dp)) {
                    SupportRow("Android 7.0+", "Temel özellikler")
                    SupportRow("Android 10+", "Gelişmiş ekran oranı")
                    SupportRow("Android 12+", "Tam destek")
                }
            }

            // Warning card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF2A1010))
                    .border(1.dp, Color(0xFF6B2020), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Warning, contentDescription = null,
                        tint = Color(0xFFFF6666), modifier = Modifier.size(18.dp).padding(top = 1.dp))
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("Önemli Not", color = Color(0xFFFF6666), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Uygulama, Android güvenlik kısıtlamaları nedeniyle diğer uygulamaların ekran çözünürlüğünü doğrudan değiştiremez. " +
                                    "Bu uygulama oyun başlatma ve ayar yönetimi sağlar.",
                            color = Color(0xFFCC9999),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .shadow(4.dp, RoundedCornerShape(18.dp), ambientColor = NeonPurple.copy(0.15f))
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF130630), Color(0xFF0A0320))))
            .border(1.dp, BorderPurple, RoundedCornerShape(18.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A0840).copy(0.6f))
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(title, color = NeonPurpleLight, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
        }
        content()
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = NeonPurple.copy(0.8f), modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, color = TextDim, fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = NeonPurple,
                uncheckedThumbColor = TextDim,
                uncheckedTrackColor = BackgroundCard
            )
        )
    }
}

@Composable
private fun SettingsInfoRow(icon: ImageVector, title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = NeonPurple.copy(0.8f), modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(title, color = TextPrimary, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(value, color = NeonPurpleLight, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SupportRow(android: String, feature: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(android, color = TextSecondary, fontSize = 12.sp)
        Text(feature, color = NeonPurpleLight.copy(0.8f), fontSize = 12.sp)
    }
}
