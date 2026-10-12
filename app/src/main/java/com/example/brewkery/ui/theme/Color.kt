package com.example.brewkery.ui.theme
/*
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

 */



import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object BrewkeryColors {
    // Exact, from the prototype CSS
    val Espresso = Color(0xFF140B07)
    val Terracotta = Color(0xFFD9532F)
    val Crema = Color(0xFFFDFAF7)
    val Border = Color(0xFFEBD8CB)
    val Ink = Color(0xFF231710)

    // Measured from the prototype screenshots
    val Surface = Color.White
    val Tint = Color(0xFFF9EBE2)
    val InkMuted = Color(0xFF645B55)
    val BadgeBackground = Color(0xFFFEF2BE)
    val BadgeText = Color(0xFF482100)
    val Amber = Color(0xFFF5A524)
    val BarLabel = Color(0xFFE4D7CF)
    val PreparingBackground = Color(0xFFFEF8DA)
    val PreparingText = Color(0xFF856445)
    val Success = Color(0xFF047857)
    val Danger = Color(0xFFDC1F48)
    val Heart = Color(0xFFF43F5E)

    /** CSS: linear-gradient(135deg, #d9532f 0%, #b84121 50%, #8c2b12 100%) */
    val TerracottaGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFD9532F), Color(0xFFB84121), Color(0xFF8C2B12)),
        start = Offset.Zero,
        end = Offset.Infinite
    )
}