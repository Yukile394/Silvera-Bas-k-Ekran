package com.silvera.basikekran

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.silvera.basikekran.data.MainViewModel
import com.silvera.basikekran.ui.screens.*
import com.silvera.basikekran.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SilveraBasikEkranTheme {
                SilveraApp()
            }
        }
    }
}

enum class Screen(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    HOME("Ana Sayfa", Icons.Default.Home, Icons.Default.Home),
    GAMES("Oyunlar", Icons.Default.SportsEsports, Icons.Default.SportsEsports),
    SETTINGS("Ayarlar", Icons.Default.Settings, Icons.Default.Settings),
    ABOUT("Hakkında", Icons.Default.Info, Icons.Default.Info)
}

@Composable
fun SilveraApp() {
    val viewModel: MainViewModel = viewModel()
    var currentScreen by remember { mutableStateOf(Screen.HOME) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDeep)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        // Screen content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 68.dp) // space for nav bar
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    slideInHorizontally(
                        initialOffsetX = { if (targetState.ordinal > initialState.ordinal) it else -it },
                        animationSpec = tween(280, easing = EaseInOutQuart)
                    ) togetherWith slideOutHorizontally(
                        targetOffsetX = { if (targetState.ordinal > initialState.ordinal) -it else it },
                        animationSpec = tween(280, easing = EaseInOutQuart)
                    )
                },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    Screen.HOME -> HomeScreen(
                        viewModel = viewModel,
                        onNavigateToGames = { currentScreen = Screen.GAMES },
                        onNavigateToSettings = { currentScreen = Screen.SETTINGS }
                    )
                    Screen.GAMES -> GamesScreen(viewModel = viewModel)
                    Screen.SETTINGS -> SettingsScreen()
                    Screen.ABOUT -> AboutScreen()
                }
            }
        }

        // Bottom Navigation
        SilveraNavBar(
            currentScreen = currentScreen,
            onScreenSelected = { currentScreen = it },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun SilveraNavBar(
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D0420).copy(0.95f),
                        Color(0xFF08031A).copy(0.98f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(Color.Transparent, BorderPurple.copy(0.6f), NeonPurple.copy(0.3f), BorderPurple.copy(0.6f), Color.Transparent)
                ),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Screen.values().forEach { screen ->
                NavBarItem(
                    screen = screen,
                    isSelected = currentScreen == screen,
                    onClick = { onScreenSelected(screen) }
                )
            }
        }
    }
}

@Composable
private fun NavBarItem(
    screen: Screen,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    val iconTint by animateColorAsState(
        targetValue = if (isSelected) NeonPurple else TextDim,
        animationSpec = tween(200),
        label = "nav_tint"
    )

    val labelColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else TextDim,
        animationSpec = tween(200),
        label = "nav_label"
    )

    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy),
        label = "nav_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 6.dp)
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .shadow(8.dp, RoundedCornerShape(10.dp),
                            ambientColor = NeonPurple.copy(0.5f),
                            spotColor = NeonPurple.copy(0.7f))
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.radialGradient(
                                listOf(NeonPurple.copy(0.25f), Color(0xFF3D1060).copy(0.4f))
                            )
                        )
                )
            }
            Icon(
                imageVector = screen.icon,
                contentDescription = screen.label,
                tint = iconTint,
                modifier = Modifier
                    .size(22.dp)
                    .then(
                        if (isSelected) Modifier.shadow(
                            4.dp, RoundedCornerShape(4.dp),
                            ambientColor = NeonPurple.copy(0.3f),
                            spotColor = NeonPurple.copy(0.5f)
                        ) else Modifier
                    )
            )
        }

        Spacer(Modifier.height(2.dp))

        Text(
            text = screen.label,
            color = labelColor,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
