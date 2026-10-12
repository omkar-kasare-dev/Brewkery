package com.example.brewkery.ui.theme



/*
import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun BrewkeryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

 */




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