package com.silvera.basikekran.data

data class Game(
    val id: String,
    val name: String,
    val packageNames: List<String>,
    val defaultWidth: Int = 1280,
    val defaultHeight: Int = 720,
    val defaultRatio: ScreenAspectRatio = ScreenAspectRatio.RATIO_4_3
)

enum class ScreenAspectRatio(val label: String, val description: String, val widthFactor: Float, val heightFactor: Float) {
    RATIO_16_9("16:9", "Oran", 16f, 9f),
    RATIO_4_3("4:3", "Oran", 4f, 3f),
    RATIO_21_9("21:9", "Oran", 21f, 9f),
    DIKEY("Dikey", "Mod", 9f, 16f),
    YATAY("Yatay", "Mod", 16f, 9f),
    OZEL("Özel", "Ayar", 0f, 0f)
}

data class GameProfile(
    val gameId: String,
    val selectedRatio: ScreenAspectRatio,
    val customWidth: Int,
    val customHeight: Int
)

object GameRepository {
    val games = listOf(
        Game(
            id = "standoff2",
            name = "Standoff 2",
            packageNames = listOf("com.standoff2.standoff2", "standoff.two"),
            defaultWidth = 1280,
            defaultHeight = 720,
            defaultRatio = ScreenAspectRatio.RATIO_4_3
        ),
        Game(
            id = "pubgmobile",
            name = "PUBG Mobile",
            packageNames = listOf("com.tencent.ig", "com.pubg.imobile", "com.rekoo.pubgm"),
            defaultWidth = 1920,
            defaultHeight = 1080,
            defaultRatio = ScreenAspectRatio.RATIO_16_9
        )
    )

    fun getById(id: String): Game? = games.find { it.id == id }
}
