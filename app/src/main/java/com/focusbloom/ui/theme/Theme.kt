package com.focusbloom.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// FocusBloom Custom Colors
val Purple80 = androidx.compose.ui.graphics.Color(0xFFD0BCFF)
val PurpleGrey80 = androidx.compose.ui.graphics.Color(0xFFCCC2DC)
val Pink80 = androidx.compose.ui.graphics.Color(0xFFEFB8C8)

val Purple40 = androidx.compose.ui.graphics.Color(0xFF6650a4)
val PurpleGrey40 = androidx.compose.ui.graphics.Color(0xFF625b71)
val Pink40 = androidx.compose.ui.graphics.Color(0xFF7D5260)

// Light Theme Colors
val LightBackground = androidx.compose.ui.graphics.Color(0xFFFFFBFE)
val LightSurface = androidx.compose.ui.graphics.Color(0xFFFFFBFE)
val LightOnBackground = androidx.compose.ui.graphics.Color(0xFF1C1B1F)
val LightOnSurface = androidx.compose.ui.graphics.Color(0xFF1C1B1F)

// Dark Theme Colors
val DarkBackground = androidx.compose.ui.graphics.Color(0xFF1C1B1F)
val DarkSurface = androidx.compose.ui.graphics.Color(0xFF1C1B1F)
val DarkOnBackground = androidx.compose.ui.graphics.Color(0xFFE6E1E5)
val DarkOnSurface = androidx.compose.ui.graphics.Color(0xFFE6E1E5)

// Primary brand colors
val FocusPrimary = androidx.compose.ui.graphics.Color(0xFF6750A4)
val FocusPrimaryDark = androidx.compose.ui.graphics.Color(0xFFD0BCFF)

// FocusBloom Color Schemes
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = LightBackground,
    surface = LightSurface,
    onBackground = LightOnBackground,
    onSurface = LightOnSurface
)

@Composable
fun FocusBloomTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disable dynamic color for consistent branding
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
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
