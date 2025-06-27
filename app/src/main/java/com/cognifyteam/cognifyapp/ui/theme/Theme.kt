package com.cognifyteam.cognifyapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// DARK THEME
val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),              // Light Blue
    onPrimary = Color(0xFF0D47A1),            // Darker Blue (for contrast)
    primaryContainer = Color(0xFF1E88E5),     // Medium Blue
    onPrimaryContainer = Color.White,

    inversePrimary = Color(0xFFCE93D8),       // Soft Purple

    secondary = Color(0xFFCE93D8),            // Light Purple
    onSecondary = Color(0xFF4A148C),          // Dark Purple
    secondaryContainer = Color(0xFFAB47BC),   // Medium Purple
    onSecondaryContainer = Color.White,

    tertiary = Color(0xFF80CBC4),             // Teal hint (educational touch)
    onTertiary = Color(0xFF004D40),
    tertiaryContainer = Color(0xFF4DB6AC),
    onTertiaryContainer = Color.White,

    error = Color(0xFFEF9A9A),
    onError = Color(0xFFB71C1C),
    errorContainer = Color(0xFFE57373),
    onErrorContainer = Color.White,

    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),

    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFF5F5F5),

    inverseSurface = Color(0xFFECEFF1),
    inverseOnSurface = Color(0xFF212121),

    surfaceVariant = Color(0xFF37474F),
    onSurfaceVariant = Color(0xFFB0BEC5),

    outline = Color(0xFF90A4AE)
)

// LIGHT THEME
val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1976D2),              // Blue
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBBDEFB),     // Light Blue
    onPrimaryContainer = Color(0xFF0D47A1),

    inversePrimary = Color(0xFFBA68C8),       // Light Purple

    secondary = Color(0xFFAB47BC),            // Purple
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE1BEE7),
    onSecondaryContainer = Color(0xFF4A148C),

    tertiary = Color(0xFF26A69A),             // Teal
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFB2DFDB),
    onTertiaryContainer = Color(0xFF004D40),

    error = Color(0xFFD32F2F),
    onError = Color.White,
    errorContainer = Color(0xFFFFCDD2),
    onErrorContainer = Color(0xFFB71C1C),

    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF212121),

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF212121),

    inverseSurface = Color(0xFF263238),
    inverseOnSurface = Color(0xFFF5F5F5),

    surfaceVariant = Color(0xFFCFD8DC),
    onSurfaceVariant = Color(0xFF455A64),

    outline = Color(0xFF90A4AE)
)

@Composable
fun CognifyApplicationTheme(
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