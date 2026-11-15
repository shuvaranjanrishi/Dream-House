package com.therishideveloper.dreamhouse.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.therishideveloper.dreamhouse.data.model.LocalStrings
import com.therishideveloper.dreamhouse.data.model.provideStrings

val LocalIsDarkTheme = compositionLocalOf { false }

private val DarkColorScheme = darkColorScheme(
    primary = lightTealAccent,
    onPrimary = Color(0xFF003731),
    primaryContainer = Color(0xFF004F47),
    onPrimaryContainer = Color(0xFFB2DFDB),
    secondary = Color(0xFF80CBC4),
    onSecondary = Color(0xFF003731),
    tertiary = Color(0xFFFFB74D),
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    surfaceContainer = DarkSurface,
    surfaceContainerHigh = DarkSurfaceVariant,
    surfaceContainerLow = DarkBackground,
    outline = DarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = tealColor,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB2DFDB),
    onPrimaryContainer = Color(0xFF00201C),
    secondary = Color(0xFF26A69A),
    onSecondary = Color.White,
    tertiary = amberAccent,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    surfaceContainer = LightSurface,
    surfaceContainerHigh = LightSurfaceVariant,
    surfaceContainerLow = LightBackground,
    outline = LightOutline
)

@Composable
fun DreamHouseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = false
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    val strings = provideStrings()

    CompositionLocalProvider(
        LocalStrings provides strings,
        LocalIsDarkTheme provides darkTheme
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}