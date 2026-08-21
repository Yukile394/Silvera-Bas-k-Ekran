package com.silvera.basikekran.data

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import com.silvera.basikekran.service.FloatingOverlayService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class LaunchResult {
    object Idle : LaunchResult()
    object Success : LaunchResult()
    data class GameNotInstalled(val gameName: String) : LaunchResult()
    data class Error(val message: String) : LaunchResult()
    object NotSupported : LaunchResult()
}

sealed class ApplyResult {
    object Idle : ApplyResult()
    data class Applied(val method: String) : ApplyResult()
    object OverlayPermissionNeeded : ApplyResult()
    object NotSupported : ApplyResult()
    data class Error(val message: String) : ApplyResult()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsManager = PreferencesManager(application)

    private val _selectedGameId = MutableStateFlow(prefsManager.loadSelectedGameId())
    val selectedGameId: StateFlow<String> = _selectedGameId.asStateFlow()

    private val _currentProfile = MutableStateFlow(
        prefsManager.loadGameProfile(_selectedGameId.value)
    )
    val currentProfile: StateFlow<GameProfile> = _currentProfile.asStateFlow()

    private val _launchResult = MutableStateFlow<LaunchResult>(LaunchResult.Idle)
    val launchResult: StateFlow<LaunchResult> = _launchResult.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val _applyResult = MutableStateFlow<ApplyResult>(ApplyResult.Idle)
    val applyResult: StateFlow<ApplyResult> = _applyResult.asStateFlow()

    private val _isOverlayActive = MutableStateFlow(false)
    val isOverlayActive: StateFlow<Boolean> = _isOverlayActive.asStateFlow()

    private val _capability = MutableStateFlow(
        DisplayResolutionManager.detectCapability(hasOverlayPermission())
    )
    val capability: StateFlow<DisplayResolutionManager.Capability> = _capability.asStateFlow()

    private fun hasOverlayPermission(): Boolean {
        val context = getApplication<Application>()
        return Settings.canDrawOverlays(context)
    }

    fun refreshCapability() {
        _capability.value = DisplayResolutionManager.detectCapability(hasOverlayPermission())
    }

    fun overlayPermissionIntent(): Intent {
        val context = getApplication<Application>()
        return Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}")
        )
    }

    /**
     * "Basık Ekran" ayarını gerçekten uygulamayı dener:
     * 1) Root varsa -> gerçek "wm size" ile sistem çözünürlüğünü değiştirir
     * 2) Root yok ama overlay izni varsa -> siyah çubuklarla görsel simülasyon başlatır
     * 3) Hiçbiri yoksa -> kullanıcıdan overlay izni ister / desteklenmiyor mesajı verir
     */
    fun applyBasikEkran(width: Int, height: Int) {
        refreshCapability()
        when (_capability.value) {
            DisplayResolutionManager.Capability.ROOT -> {
                val result = DisplayResolutionManager.applyResolutionRoot(width, height)
                result.fold(
                    onSuccess = {
                        _applyResult.value = ApplyResult.Applied("root")
                        _snackbarMessage.value = "Çözünürlük gerçek zamanlı olarak ${width}×${height} yapıldı"
                    },
                    onFailure = { e ->
                        _applyResult.value = ApplyResult.Error(e.message ?: "Bilinmeyen hata")
                        _snackbarMessage.value = "Uygulanamadı: ${e.message}"
                    }
                )
            }
            DisplayResolutionManager.Capability.OVERLAY_ONLY -> {
                applyOverlaySimulation(width, height)
            }
            DisplayResolutionManager.Capability.SECURE_SETTINGS -> {
                applyOverlaySimulation(width, height)
            }
            DisplayResolutionManager.Capability.NONE -> {
                if (!hasOverlayPermission()) {
                    _applyResult.value = ApplyResult.OverlayPermissionNeeded
                } else {
                    _applyResult.value = ApplyResult.NotSupported
                    _snackbarMessage.value = "Bu cihazda ekran oranı değiştirme özelliği desteklenmiyor."
                }
            }
        }
    }

    private fun applyOverlaySimulation(width: Int, height: Int) {
        val context = getApplication<Application>()
        val targetRatio = width.toFloat() / height.toFloat()
        // Ekranın gerçek en/boy oranına göre üstten/alttan ne kadar kırpılacağını hesapla
        val metrics = context.resources.displayMetrics
        val screenRatio = metrics.widthPixels.toFloat() / metrics.heightPixels.toFloat()

        val barHeightPercent = if (targetRatio > screenRatio) {
            // Hedef oran daha geniş -> üst/alttan siyah çubuk gerekir
            val idealHeightForWidth = metrics.widthPixels / targetRatio
            val totalBarPx = metrics.heightPixels - idealHeightForWidth
            (totalBarPx / 2f) / metrics.heightPixels
        } else {
            0f
        }

        try {
            FloatingOverlayService.start(context, barHeightPercent.coerceIn(0f, 0.35f))
            _isOverlayActive.value = true
            _applyResult.value = ApplyResult.Applied("overlay")
            _snackbarMessage.value = "Basık ekran görsel simülasyonu aktif (${width}×${height})"
        } catch (e: Exception) {
            _applyResult.value = ApplyResult.Error(e.message ?: "Overlay başlatılamadı")
        }
    }

    fun stopOverlaySimulation() {
        val context = getApplication<Application>()
        FloatingOverlayService.stop(context)
        _isOverlayActive.value = false
        _snackbarMessage.value = "Basık ekran simülasyonu kapatıldı"
    }

    fun resetResolution() {
        if (_capability.value == DisplayResolutionManager.Capability.ROOT) {
            DisplayResolutionManager.resetResolutionRoot().fold(
                onSuccess = { _snackbarMessage.value = "Çözünürlük varsayılana sıfırlandı" },
                onFailure = { e -> _snackbarMessage.value = "Sıfırlama başarısız: ${e.message}" }
            )
        }
        if (_isOverlayActive.value) {
            stopOverlaySimulation()
        }
    }

    fun clearApplyResult() {
        _applyResult.value = ApplyResult.Idle
    }

    fun selectGame(gameId: String) {
        _selectedGameId.value = gameId
        _currentProfile.value = prefsManager.loadGameProfile(gameId)
        prefsManager.saveSelectedGameId(gameId)
    }

    fun selectRatio(ratio: AspectRatio) {
        val current = _currentProfile.value
        val updated = current.copy(selectedRatio = ratio)
        _currentProfile.value = updated
        prefsManager.saveGameProfile(updated)
        _snackbarMessage.value = "${ratio.label} oranı seçildi"
    }

    fun updateCustomDimensions(width: Int, height: Int) {
        val current = _currentProfile.value
        val updated = current.copy(
            customWidth = width,
            customHeight = height,
            selectedRatio = AspectRatio.OZEL
        )
        _currentProfile.value = updated
        prefsManager.saveGameProfile(updated)
        _snackbarMessage.value = "Özel boyut kaydedildi: ${width}×${height}"
    }

    fun validateAndSaveDimensions(widthStr: String, heightStr: String): Boolean {
        val width = widthStr.toIntOrNull()
        val height = heightStr.toIntOrNull()
        return when {
            width == null || height == null -> {
                _snackbarMessage.value = "Lütfen geçerli sayısal değerler girin"
                false
            }
            width < 240 || width > 3840 -> {
                _snackbarMessage.value = "Genişlik 240-3840 arasında olmalıdır"
                false
            }
            height < 240 || height > 2160 -> {
                _snackbarMessage.value = "Yükseklik 240-2160 arasında olmalıdır"
                false
            }
            else -> {
                updateCustomDimensions(width, height)
                true
            }
        }
    }

    fun launchGame() {
        val context = getApplication<Application>()
        val game = GameRepository.getById(_selectedGameId.value)
        if (game == null) {
            _launchResult.value = LaunchResult.Error("Oyun bulunamadı")
            return
        }

        val pm: PackageManager = context.packageManager
        var launchIntent: Intent? = null

        for (pkg in game.packageNames) {
            try {
                launchIntent = pm.getLaunchIntentForPackage(pkg)
                if (launchIntent != null) break
            } catch (e: Exception) {
                // try next package name
            }
        }

        if (launchIntent == null) {
            _launchResult.value = LaunchResult.GameNotInstalled(game.name)
            return
        }

        try {
            // Kayıtlı basık ekran profili varsa oyun başlamadan önce uygula
            val profile = _currentProfile.value
            if (profile.selectedRatio != AspectRatio.RATIO_16_9 || profile.customWidth != game.defaultWidth) {
                applyBasikEkran(profile.customWidth, profile.customHeight)
            }
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
            _launchResult.value = LaunchResult.Success
            _snackbarMessage.value = "${game.name} başlatılıyor..."
        } catch (e: Exception) {
            _launchResult.value = LaunchResult.Error("Oyun başlatılamadı: ${e.localizedMessage}")
        }
    }

    fun clearLaunchResult() {
        _launchResult.value = LaunchResult.Idle
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun getSelectedGame(): Game? = GameRepository.getById(_selectedGameId.value)
}
