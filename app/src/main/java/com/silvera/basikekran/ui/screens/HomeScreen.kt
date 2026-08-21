package com.silvera.basikekran.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.silvera.basikekran.data.*
import com.silvera.basikekran.ui.components.AnimatedBackground
import com.silvera.basikekran.ui.components.GlowCard
import com.silvera.basikekran.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToGames: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val selectedGameId by viewModel.selectedGameId.collectAsState()
    val currentProfile by viewModel.currentProfile.collectAsState()
    val launchResult by viewModel.launchResult.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show snackbar on messages
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    // Handle launch result dialogs
    var showDialog by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(launchResult) {
        when (val r = launchResult) {
            is LaunchResult.GameNotInstalled ->
                showDialog = "\"${r.gameName}\" cihazınızda yüklü değil."
            is LaunchResult.Error ->
                showDialog = r.message
            is LaunchResult.NotSupported ->
                showDialog = "Bu cihazda ekran oranı değiştirme özelliği desteklenmiyor."
            else -> {}
        }
        if (launchResult != LaunchResult.Idle) viewModel.clearLaunchResult()
    }

    showDialog?.let { msg ->
        AlertDialog(
            onDismissRequest = { showDialog = null },
            containerColor = BackgroundCard,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text("Bilgi", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(msg, color = TextSecondary)
            },
            confirmButton = {
                TextButton(onClick = { showDialog = null }) {
                    Text("Tamam", color = NeonPurple, fontWeight = FontWeight.Bold)
                }
            }
        )
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
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedBackground()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 16.dp)
            ) {
                // Header
                HomeHeader(onSettingsClick = onNavigateToSettings)

                Spacer(modifier = Modifier.height(20.dp))

                // Desteklenen Oyunlar
                SupportedGamesSection(
                    selectedGameId = selectedGameId,
                    onGameSelect = { viewModel.selectGame(it) },
                    onAyarla = onNavigateToGames
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Hızlı Ayarlar
                QuickSettingsSection(
                    selectedRatio = currentProfile.selectedRatio,
                    onRatioSelected = { viewModel.selectRatio(it) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Oyunu Başlat
                LaunchButton(
                    selectedGame = viewModel.getSelectedGame(),
                    onClick = { viewModel.launchGame() }
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun HomeHeader(onSettingsClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "header_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(2500, easing = EaseInOutSine), RepeatMode.Reverse
        ), label = "glow"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A0840),
                        Color(0xFF0D0320),
                        Color.Transparent
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        // Glow circle behind icon
        Box(
            modifier = Modifier
                .size(140.dp)
                .align(Alignment.CenterStart)
                .offset(x = (-10).dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF9B3DFF).copy(alpha = glowAlpha * 0.6f),
                            Color.Transparent
                        )
                    ),
                    radius = size.minDimension / 1.5f
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Game controller icon with glow
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(72.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = CircleShape,
                        ambientColor = NeonPurple.copy(alpha = 0.8f),
                        spotColor = NeonPurple
                    )
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF5A1FA0), Color(0xFF2D0860))
                        )
                    )
                    .border(1.5.dp, NeonPurpleLight.copy(alpha = 0.7f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.SportsEsports,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Title column
            Column(modifier = Modifier.weight(1f)) {
                // Silvera brand
                Text(
                    text = "Silvera",
                    color = NeonPurpleLight,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 3.sp
                )
                // Basık Ekran main title
                Text(
                    text = "Basık Ekran",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp,
                    lineHeight = 32.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Mobil oyunlarda daha iyi ekran deneyimi",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            // Icon buttons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HeaderIconButton(icon = Icons.Default.Settings, onClick = {})
                HeaderIconButton(icon = Icons.Default.Info, onClick = {})
            }
        }
    }
}

@Composable
private fun HeaderIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.9f else 1f, label = "btn_scale")

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(38.dp)
            .scale(scale)
            .shadow(6.dp, RoundedCornerShape(10.dp), ambientColor = NeonPurple.copy(0.4f), spotColor = NeonPurple.copy(0.5f))
            .clip(RoundedCornerShape(10.dp))
            .background(BackgroundCard)
            .border(1.dp, BorderPurple, RoundedCornerShape(10.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = NeonPurpleLight, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun SupportedGamesSection(
    selectedGameId: String,
    onGameSelect: (String) -> Unit,
    onAyarla: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        // Section header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.SportsEsports,
                contentDescription = null,
                tint = NeonPurple,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Desteklenen Oyunlar",
                color = TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GameRepository.games.forEach { game ->
                GameCard(
                    game = game,
                    isSelected = selectedGameId == game.id,
                    onSelect = { onGameSelect(game.id) },
                    onAyarla = onAyarla,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun GameCard(
    game: com.silvera.basikekran.data.Game,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onAyarla: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.95f
            isSelected -> 1.02f
            else -> 1f
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_scale"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) NeonPurple else BorderPurple,
        animationSpec = tween(300),
        label = "border"
    )

    val elevation by animateDpAsState(
        targetValue = if (isSelected) 16.dp else 6.dp,
        label = "elevation"
    )

    Column(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(16.dp),
                ambientColor = if (isSelected) NeonPurple.copy(0.5f) else Color.Transparent,
                spotColor = if (isSelected) NeonPurple.copy(0.7f) else Color.Transparent
            )
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = if (game.id == "standoff2")
                        listOf(Color(0xFF1E0840), Color(0xFF0D0425))
                    else
                        listOf(Color(0xFF071830), Color(0xFF040E1E))
                )
            )
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onSelect() }
            .padding(bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Game image area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                .background(
                    Brush.linearGradient(
                        colors = if (game.id == "standoff2")
                            listOf(Color(0xFF3D0F60), Color(0xFF1A0840), Color(0xFF2D0A4A))
                        else
                            listOf(Color(0xFF0A2050), Color(0xFF051228), Color(0xFF0E2848))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Abstract game art
            Canvas(modifier = Modifier.fillMaxSize()) {
                if (game.id == "standoff2") {
                    drawStandoff2Art(this)
                } else {
                    drawPubgArt(this)
                }
            }
            // Game icon overlay
            Icon(
                imageVector = if (game.id == "standoff2") Icons.Default.GpsFixed else Icons.Default.TrackChanges,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.15f),
                modifier = Modifier.size(56.dp)
            )
            // Gradient overlay at bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent,
                                if (game.id == "standoff2") Color(0xFF1E0840) else Color(0xFF071830))
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = game.name,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Ayarla button
        val btnInteraction = remember { MutableInteractionSource() }
        val btnPressed by btnInteraction.collectIsPressedAsState()
        val btnScale by animateFloatAsState(if (btnPressed) 0.93f else 1f, label = "btn")

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .scale(btnScale)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFF5A1FA0), Color(0xFF3A0E70))
                    )
                )
                .border(1.dp, NeonPurple.copy(0.5f), RoundedCornerShape(20.dp))
                .clickable(interactionSource = btnInteraction, indication = null) { onAyarla() }
                .padding(horizontal = 20.dp, vertical = 6.dp)
        ) {
            Text("Ayarla", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

private fun drawStandoff2Art(scope: DrawScope) {
    with(scope) {
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0xFF8B2FE8).copy(0.5f), Color.Transparent),
                center = Offset(size.width * 0.3f, size.height * 0.3f),
                radius = size.width * 0.4f
            ),
            radius = size.width * 0.4f,
            center = Offset(size.width * 0.3f, size.height * 0.3f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0xFFFF4444).copy(0.3f), Color.Transparent),
                center = Offset(size.width * 0.7f, size.height * 0.6f),
                radius = size.width * 0.3f
            ),
            radius = size.width * 0.3f,
            center = Offset(size.width * 0.7f, size.height * 0.6f)
        )
    }
}

private fun drawPubgArt(scope: DrawScope) {
    with(scope) {
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0xFF2255CC).copy(0.4f), Color.Transparent),
                center = Offset(size.width * 0.7f, size.height * 0.3f),
                radius = size.width * 0.45f
            ),
            radius = size.width * 0.45f,
            center = Offset(size.width * 0.7f, size.height * 0.3f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0xFF22AA44).copy(0.25f), Color.Transparent),
                center = Offset(size.width * 0.3f, size.height * 0.7f),
                radius = size.width * 0.35f
            ),
            radius = size.width * 0.35f,
            center = Offset(size.width * 0.3f, size.height * 0.7f)
        )
    }
}

@Composable
private fun QuickSettingsSection(
    selectedRatio: AspectRatio,
    onRatioSelected: (AspectRatio) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .shadow(8.dp, RoundedCornerShape(20.dp), ambientColor = NeonPurple.copy(0.2f), spotColor = NeonPurple.copy(0.3f))
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF130630), Color(0xFF0A0320))
                )
            )
            .border(1.dp, BorderPurple, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Settings, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Hızlı Ayarlar", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(14.dp))

        val ratios = AspectRatio.values()
        val rows = ratios.toList().chunked(3)

        rows.forEach { rowRatios ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowRatios.forEach { ratio ->
                    RatioChip(
                        ratio = ratio,
                        isSelected = selectedRatio == ratio,
                        onClick = { onRatioSelected(ratio) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill remaining space if row is incomplete
                repeat(3 - rowRatios.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            if (rowRatios != rows.last()) Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun RatioChip(
    ratio: AspectRatio,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy),
        label = "chip_scale"
    )

    val bgBrush by remember(isSelected) {
        derivedStateOf {
            if (isSelected)
                Brush.verticalGradient(listOf(Color(0xFF6B20C0), Color(0xFF3D0E80)))
            else
                Brush.verticalGradient(listOf(Color(0xFF1A0838), Color(0xFF0D0420)))
        }
    }

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) NeonPurple else UnselectedBorder,
        animationSpec = tween(250),
        label = "chip_border"
    )

    val elevation by animateDpAsState(if (isSelected) 10.dp else 2.dp, label = "chip_elev")

    Box(
        modifier = modifier
            .height(62.dp)
            .scale(scale)
            .shadow(elevation, RoundedCornerShape(12.dp),
                ambientColor = if (isSelected) NeonPurple.copy(0.5f) else Color.Transparent,
                spotColor = if (isSelected) NeonPurple.copy(0.7f) else Color.Transparent)
            .clip(RoundedCornerShape(12.dp))
            .background(bgBrush)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = ratio.label,
                color = if (isSelected) Color.White else TextSecondary,
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
            )
            Text(
                text = ratio.description,
                color = if (isSelected) NeonPurpleLight else TextDim,
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
private fun LaunchButton(
    selectedGame: com.silvera.basikekran.data.Game?,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy),
        label = "launch_scale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "btn_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1800, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "glow"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .scale(scale)
            .shadow(
                elevation = (16 * glowAlpha).dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = NeonPurple.copy(alpha = glowAlpha * 0.6f),
                spotColor = NeonPurple.copy(alpha = glowAlpha * 0.8f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF7B2FBE),
                        Color(0xFF9B3DFF),
                        Color(0xFF6B1FBF)
                    )
                )
            )
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Oyunu Başlat",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
            )
        }
    }

    if (selectedGame != null) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Seçili: ${selectedGame.name}",
            color = TextDim,
            fontSize = 11.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}
