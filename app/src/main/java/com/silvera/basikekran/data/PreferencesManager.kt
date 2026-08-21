package com.silvera.basikekran.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("silvera_basik_ekran_prefs", Context.MODE_PRIVATE)

    fun saveGameProfile(profile: GameProfile) {
        prefs.edit().apply {
            putString("${profile.gameId}_ratio", profile.selectedRatio.name)
            putInt("${profile.gameId}_width", profile.customWidth)
            putInt("${profile.gameId}_height", profile.customHeight)
            apply()
        }
    }

    fun loadGameProfile(gameId: String): GameProfile {
        val game = GameRepository.getById(gameId)
        val ratioName = prefs.getString("${gameId}_ratio", game?.defaultRatio?.name ?: ScreenAspectRatio.RATIO_16_9.name)
        val ratio = try {
            ScreenAspectRatio.valueOf(ratioName ?: ScreenAspectRatio.RATIO_16_9.name)
        } catch (e: IllegalArgumentException) {
            ScreenAspectRatio.RATIO_16_9
        }
        val width = prefs.getInt("${gameId}_width", game?.defaultWidth ?: 1280)
        val height = prefs.getInt("${gameId}_height", game?.defaultHeight ?: 720)
        return GameProfile(gameId, ratio, width, height)
    }

    fun saveSelectedGameId(gameId: String) {
        prefs.edit().putString("selected_game_id", gameId).apply()
    }

    fun loadSelectedGameId(): String {
        return prefs.getString("selected_game_id", "standoff2") ?: "standoff2"
    }
}
