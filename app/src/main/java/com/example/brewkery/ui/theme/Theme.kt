package com.example.brewkery.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = com.example.brewkery.ui.theme.BrewkeryColors.Terracotta,
    onPrimary = Color.White,
    secondary = com.example.brewkery.ui.theme.BrewkeryColors.Espresso,
    onSecondary = Color.White,
    background = com.example.brewkery.ui.theme.BrewkeryColors.Crema,
    onBackground = com.example.brewkery.ui.theme.BrewkeryColors.Ink,
    surface = com.example.brewkery.ui.theme.BrewkeryColors.Surface,
    onSurface = com.example.brewkery.ui.theme.BrewkeryColors.Ink,
    outline = com.example.brewkery.ui.theme.BrewkeryColors.Border,
    error = BrewkeryColors.Danger
)

/** Radii measured from the prototype screenshots. */
object BrewkeryShapes {
    val Card = RoundedCornerShape(12.dp)
    val Panel = RoundedCornerShape(16.dp)
    val Button = RoundedCornerShape(12.dp)
}

/** The prototype is light-only, so there is no dark scheme. */
@Composable
fun BrewkeryTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LightColors) {
        Surface(color = MaterialTheme.colorScheme.background, content = content)
    }
}