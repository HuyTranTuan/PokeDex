package com.jetpack.pokedex.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val Crimson = Color(0xFFCB092F)
val LightGrey = Color(0xFFC9C9C9)
val DarkGrey = Color(0xFF1A1A1A)

//Status bar
val LT50 = Color(0xFFEF0000)
val LT100 = Color(0xFFBD3817)
val LT150 = Color(0xFFECB100)
val LT200 = Color(0xFFB6FF0F)
val LT250 = Color(0xFF30E000)
val LT300 = Color(0xFFB000D9)


val normal = hexToComposeColor("#A8A77A")
val fire = hexToComposeColor("#EE8130")
val water = hexToComposeColor("#6390F0")
val electric = hexToComposeColor("#F7D02C")
val grass = hexToComposeColor("#7AC74C")
val ice = hexToComposeColor("#96D9D6")
val fighting = hexToComposeColor("#C22E28")
val poison = hexToComposeColor("#A33EA1")
val ground = hexToComposeColor("#E2BF65")
val flying = hexToComposeColor("#A98FF3")
val psychic = hexToComposeColor("#F95587")
val bug = hexToComposeColor("#A6B91A")
val rock = hexToComposeColor("#B6A136")
val ghost = hexToComposeColor("#735797")
val dragon = hexToComposeColor("#6F35FC")
val dark = hexToComposeColor("#705746")
val steel = hexToComposeColor("#B7B7CE")
val fairy = hexToComposeColor("#D685AD")
val shadow = hexToComposeColor("#130949")
val stellar = hexToComposeColor("#46647E")
val unknown = hexToComposeColor("#04b0A7")

fun hexToComposeColor(hexString: String): Color {
    // Remove '#' prefix if present and ensure it's a valid hex string
    val cleanHexString = hexString.removePrefix("#")

    // Prepend "FF" for full opacity if no alpha channel is provided
    val fullHexString = if (cleanHexString.length == 6) {
        "FF$cleanHexString"
    } else {
        cleanHexString
    }

    // Convert to Long and create Color object
    return Color(fullHexString.toLong(16))
}