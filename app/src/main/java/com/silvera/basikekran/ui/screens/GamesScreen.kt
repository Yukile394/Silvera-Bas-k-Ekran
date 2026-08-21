package com.silvera.basikekran.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.silvera.basikekran.data.*
import com.silvera.basikekran.ui.components.AnimatedBackground
import com.silvera.basikekran.ui.theme.*
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.AspectRatio as AspectRatioIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesScreen(viewModel: MainViewModel) {
    val selectedGameId by viewModel.selectedGameId.collectAsState()
    val currentProfile by viewModel.currentProfile.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val capability by viewModel.capability.collectAsState()
    val applyResult by viewModel.applyResult.collectAsState()
    val isOverlayActive by viewModel.isOverlayActive.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val overlayPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.refreshCapability()
    }

    LaunchedEffect(Unit) { viewModel.refreshCapability() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    var showPermissionDialog by remember { mutableStateOf(false) }
    LaunchedEffect(applyResult) {
        if (applyResult is ApplyResult.OverlayPermissionNeeded) {
            showPermissionDialog = true
        }
        if (applyResult !is ApplyResult.Idle) viewModel.clearApplyResult()
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            containerColor = BackgroundCard,
            shape = RoundedCornerShape(20.dp),
            title = { Text("İzin Gerekli", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Basık ekran simülasyonunu gösterebilmek için \"diğer uygulamaların üzerinde görüntüle\" iznine ihtiyaç var.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionDialog = false
                    overlayPermissionLauncher.launch(viewModel.overlayPermissionIntent())
                }) { Text("İzin Ver", color = NeonPurple, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Vazgeç", color = TextDim)
                }
            }
        )
    }

    var customWidthText by remember(currentProfile) {
        mutableStateOf(currentProfile.customWidth.toString())
    }
    var customHeightText by remember(currentProfile) {
        mutableStateOf(currentProfile.customHeight.toString())
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = BackgroundCardLight,
                    contentColor = TextPrimary,
                    actionColor = NeonPurple,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedBackground()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Section title
                Text(
                    "Oyun Ayarları",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                // Capability / method status card
                CapabilityStatusCard(capability = capability, isOverlayActive = isOverlayActive)

                // Game selector
                GameSelectorRow(
                    selectedGameId = selectedGameId,
                    onGameSelect = { viewModel.selectGame(it) }
                )

                // Aspect ratio selector
                RatioSelectorCard(
                    selectedRatio = currentProfile.selectedRatio,
                    onRatioSelected = { viewModel.selectRatio(it) }
                )

                // Live aspect-ratio preview
                RatioPreviewCard(
                    widthText = customWidthText,
                    heightText = customHeightText
                )

                // Custom dimensions
                CustomDimensionsCard(
                    widthText = customWidthText,
                    heightText = customHeightText,
                    onWidthChange = { customWidthText = it },
                    onHeightChange = { customHeightText = it },
                    onSave = {
                        viewModel.validateAndSaveDimensions(customWidthText, customHeightText)
                    },
                    onApply = {
                        val w = customWidthText.toIntOrNull()
                        val h = customHeightText.toIntOrNull()
                        if (w != null && h != null && viewModel.validateAndSaveDimensions(customWidthText, customHeightText)) {
                            viewModel.applyBasikEkran(w, h)
                        }
                    },
                    isOverlayActive = isOverlayActive,
                    onStop = { viewModel.stopOverlaySimulation() }
                )

                // Current profile summary
                ProfileSummaryCard(currentProfile = currentProfile, viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun GameSelectorRow(selectedGameId: String, onGameSelect: (String) -> Unit) {
    Column(
        modifier = Modifier
            .shadow(6.dp, RoundedCornerShape(18.dp), ambientColor = NeonPurple.copy(0.2f))
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF130630), Color(0xFF0A0320))))
            .border(1.dp, BorderPurple, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Text("Oyun Seç", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            GameRepository.games.forEach { game ->
                val isSelected = selectedGameId == game.id
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val scale by animateFloatAsState(if (isPressed) 0.95f else 1f, label = "gs")

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .scale(scale)
                        .shadow(if (isSelected) 10.dp else 2.dp, RoundedCornerShape(12.dp),
                            ambientColor = if (isSelected) NeonPurple.copy(0.5f) else Color.Transparent,
                            spotColor = if (isSelected) NeonPurple.copy(0.7f) else Color.Transparent)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected)
                                Brush.horizontalGradient(listOf(Color(0xFF6B20C0), Color(0xFF3D0E80)))
                            else
                                Brush.horizontalGradient(listOf(Color(0xFF1A0838), Color(0xFF0D0420)))
                        )
                        .border(1.dp, if (isSelected) NeonPurple else UnselectedBorder, RoundedCornerShape(12.dp))
                        .clickable(interactionSource = interactionSource, indication = null) { onGameSelect(game.id) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        game.name,
                        color = if (isSelected) Color.White else TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun RatioSelectorCard(selectedRatio: AspectRatio, onRatioSelected: (AspectRatio) -> Unit) {
    Column(
        modifier = Modifier
            .shadow(6.dp, RoundedCornerShape(18.dp), ambientColor = NeonPurple.copy(0.2f))
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF130630), Color(0xFF0A0320))))
            .border(1.dp, BorderPurple, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AspectRatioIcon, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Ekran Oranı", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))

        AspectRatio.values().chunked(3).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { ratio ->
                    val isSelected = selectedRatio == ratio
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(if (isPressed) 0.94f else 1f, label = "rs")

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .scale(scale)
                            .shadow(if (isSelected) 8.dp else 0.dp, RoundedCornerShape(10.dp),
                                ambientColor = if (isSelected) NeonPurple.copy(0.5f) else Color.Transparent,
                                spotColor = if (isSelected) NeonPurple else Color.Transparent)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected)
                                Brush.verticalGradient(listOf(Color(0xFF6B20C0), Color(0xFF3D0E80)))
                            else
                                Brush.verticalGradient(listOf(Color(0xFF1A0838), Color(0xFF0D0420))))
                            .border(1.dp, if (isSelected) NeonPurple else UnselectedBorder, RoundedCornerShape(10.dp))
                            .clickable(interactionSource = interactionSource, indication = null) { onRatioSelected(ratio) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(ratio.label, color = if (isSelected) Color.White else TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(ratio.description, color = if (isSelected) NeonPurpleLight else TextDim, fontSize = 9.sp)
                        }
                    }
                }
                repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomDimensionsCard(
    widthText: String,
    heightText: String,
    onWidthChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onSave: () -> Unit,
    onApply: () -> Unit = {},
    isOverlayActive: Boolean = false,
    onStop: () -> Unit = {}
) {
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = NeonPurple,
        unfocusedBorderColor = BorderPurple,
        focusedLabelColor = NeonPurpleLight,
        unfocusedLabelColor = TextSecondary,
        cursorColor = NeonPurple,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextSecondary,
        focusedContainerColor = BackgroundCard,
        unfocusedContainerColor = BackgroundCard
    )

    Column(
        modifier = Modifier
            .shadow(6.dp, RoundedCornerShape(18.dp), ambientColor = NeonPurple.copy(0.2f))
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF130630), Color(0xFF0A0320))))
            .border(1.dp, BorderPurple, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Tune, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Basık Ekran Ayarları", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(14.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = widthText,
                onValueChange = { if (it.length <= 4) onWidthChange(it.filter { c -> c.isDigit() }) },
                label = { Text("Genişlik") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = textFieldColors,
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                prefix = { Text("W:", color = TextDim, fontSize = 12.sp) }
            )
            OutlinedTextField(
                value = heightText,
                onValueChange = { if (it.length <= 4) onHeightChange(it.filter { c -> c.isDigit() }) },
                label = { Text("Yükseklik") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = textFieldColors,
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                prefix = { Text("H:", color = TextDim, fontSize = 12.sp) }
            )
        }

        Spacer(Modifier.height(6.dp))
        Text(
            "Desteklenen: 240×240 – 3840×2160",
            color = TextDim,
            fontSize = 10.sp
        )

        Spacer(Modifier.height(14.dp))

        // Save button
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "save_scale")

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .scale(scale)
                .shadow(8.dp, RoundedCornerShape(14.dp), ambientColor = NeonPurple.copy(0.4f), spotColor = NeonPurple.copy(0.6f))
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFF6B20C0), Color(0xFF9B3DFF), Color(0xFF6B20C0))))
                .clickable(interactionSource = interactionSource, indication = null) { onSave() },
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Text("Kaydet", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(10.dp))

        // Apply / Stop row
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            val applyInteraction = remember { MutableInteractionSource() }
            val applyPressed by applyInteraction.collectIsPressedAsState()
            val applyScale by animateFloatAsState(if (applyPressed) 0.96f else 1f, label = "apply_scale")

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .scale(applyScale)
                    .shadow(8.dp, RoundedCornerShape(14.dp), ambientColor = NeonPurpleLight.copy(0.5f), spotColor = NeonPurpleLight.copy(0.7f))
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.5.dp, NeonPurpleLight, RoundedCornerShape(14.dp))
                    .background(Color(0xFF1A0838))
                    .clickable(interactionSource = applyInteraction, indication = null) { onApply() },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.AspectRatioIcon, contentDescription = null, tint = NeonPurpleLight, modifier = Modifier.size(16.dp))
                    Text("Basık Ekranı Uygula", color = NeonPurpleLight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (isOverlayActive) {
                val stopInteraction = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .height(48.dp)
                        .width(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF2A1010))
                        .border(1.dp, Color(0xFF6B2020), RoundedCornerShape(14.dp))
                        .clickable(interactionSource = stopInteraction, indication = null) { onStop() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Durdur", tint = Color(0xFFFF6666), modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
private fun CapabilityStatusCard(
    capability: DisplayResolutionManager.Capability,
    isOverlayActive: Boolean
) {
    val (label, desc, color) = when (capability) {
        DisplayResolutionManager.Capability.ROOT ->
            Triple("Root Yöntemi Aktif", "Gerçek sistem çözünürlüğü değiştirilebilir", Color(0xFF4ADE80))
        DisplayResolutionManager.Capability.SECURE_SETTINGS ->
            Triple("Gelişmiş Yöntem Aktif", "Sistem ayarları üzerinden uygulanabilir", Color(0xFF4ADE80))
        DisplayResolutionManager.Capability.OVERLAY_ONLY ->
            Triple("Görsel Simülasyon Modu", "Root yok — siyah çubuklu basık ekran gösterimi kullanılacak", NeonPurpleLight)
        DisplayResolutionManager.Capability.NONE ->
            Triple("İzin Gerekli", "Basık ekranı uygulamak için üst pencere izni gerekiyor", Color(0xFFFFA666))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF0D0420))
            .border(1.dp, color.copy(0.4f), RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(if (isOverlayActive) Color(0xFF4ADE80) else color)
        )
        Spacer(Modifier.width(10.dp))
        Column {
            Text(label, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(desc, color = TextDim, fontSize = 11.sp)
        }
    }
}

@Composable
private fun RatioPreviewCard(widthText: String, heightText: String) {
    val w = widthText.toIntOrNull() ?: 0
    val h = heightText.toIntOrNull() ?: 0
    val ratio = if (w > 0 && h > 0) w.toFloat() / h.toFloat() else 16f / 9f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(18.dp), ambientColor = NeonPurple.copy(0.15f))
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF130630), Color(0xFF0A0320))))
            .border(1.dp, BorderPurple, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Visibility, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Önizleme", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Text(
                text = "%.2f".format(ratio) + " : 1",
                color = NeonPurpleLight,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            val boxWidthRatio = if (ratio >= (16f / 9f)) 1f else ratio / (16f / 9f)
            Box(
                modifier = Modifier
                    .fillMaxWidth(boxWidthRatio.coerceIn(0.2f, 1f))
                    .fillMaxHeight()
                    .background(
                        Brush.verticalGradient(listOf(Color(0xFF3D1A6E), Color(0xFF1A0838)))
                    )
                    .border(1.dp, NeonPurple.copy(0.6f), RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("${w}×${h}", color = TextSecondary, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun ProfileSummaryCard(currentProfile: GameProfile, viewModel: MainViewModel) {
    val game = viewModel.getSelectedGame()

    Column(
        modifier = Modifier
            .shadow(4.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF0F0530), Color(0xFF070220))))
            .border(1.dp, BorderPurple.copy(0.5f), RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, contentDescription = null, tint = NeonPurple.copy(0.7f), modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("Mevcut Profil", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(10.dp))

        ProfileRow("Oyun", game?.name ?: "-")
        ProfileRow("Oran", currentProfile.selectedRatio.label)
        ProfileRow("Boyut", "${currentProfile.customWidth} × ${currentProfile.customHeight}")
    }
}

@Composable
private fun ProfileRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextDim, fontSize = 12.sp)
        Text(value, color = NeonPurpleLight, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
