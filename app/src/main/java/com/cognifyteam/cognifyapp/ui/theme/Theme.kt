package com.cognifyteam.cognifyapp.ui.theme

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

// Definisikan warna utama Anda di sini agar mudah digunakan kembali
val CognifyPrimaryBlue = Color(0xFF1F2343)
val CognifyLightBlue = Color(0xFF535A8F) // Versi lebih terang untuk container
val CognifyOnPrimaryText = Color.White

// Warna sekunder (contoh: ungu lembut)
val CognifySecondaryPurple = Color(0xFFBA8BDB)
val CognifyOnSecondaryText = Color(0xFF33004A)
val CognifySecondaryContainer = Color(0xFF4A148C)


// LIGHT THEME (Mode Terang)
private val LightColorScheme = lightColorScheme(
    primary = CognifyPrimaryBlue,             // Warna utama: Biru Gelap
    onPrimary = CognifyOnPrimaryText,         // Teks di atas warna utama: Putih
    primaryContainer = CognifyLightBlue,      // Warna container yang lebih terang
    onPrimaryContainer = CognifyOnPrimaryText,// Teks di atas container

    secondary = CognifySecondaryPurple,       // Warna sekunder
    onSecondary = CognifyOnSecondaryText,     // Teks di atas warna sekunder
    secondaryContainer = CognifySecondaryContainer,
    onSecondaryContainer = Color.White,

    background = Color(0xFFFDFDFD),           // Background sedikit off-white
    onBackground = Color(0xFF1A1C1E),         // Teks di atas background (hampir hitam)

    surface = Color(0xFFFDFDFD),              // Warna dasar kartu, dialog, dll.
    onSurface = Color(0xFF1A1C1E),            // Teks di atas surface

    surfaceVariant = Color(0xFFE0E2EC),       // Warna untuk komponen yang tidak terlalu menonjol (misal: chip)
    onSurfaceVariant = Color(0xFF44474F),     // Teks di atas surfaceVariant

    outline = Color(0xFF74777F),              // Border untuk OutlinedTextField, dll.

    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B)
)


// DARK THEME (Mode Gelap)
private val DarkColorScheme = darkColorScheme(
    primary = CognifyLightBlue,               // Di mode gelap, primary biasanya lebih terang agar menonjol
    onPrimary = CognifyOnPrimaryText,
    primaryContainer = CognifyPrimaryBlue,    // Container menggunakan warna dasar yang lebih gelap
    onPrimaryContainer = Color(0xFFE0E0FF),

    secondary = CognifySecondaryPurple,
    onSecondary = CognifyOnSecondaryText,
    secondaryContainer = CognifySecondaryContainer,
    onSecondaryContainer = Color.White,

    background = Color(0xFF121212),           // Background khas mode gelap
    onBackground = Color(0xFFE2E2E6),         // Teks di atas background (putih keabuan)

    surface = Color(0xFF1A1C1E),              // Warna dasar kartu, dll. di mode gelap
    onSurface = Color(0xFFE2E2E6),

    surfaceVariant = Color(0xFF44474F),
    onSurfaceVariant = Color(0xFFC4C6D0),

    outline = Color(0xFF8E9099),

    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC)
)


@Composable
fun CognifyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color adalah fitur Android 12+ yang mengambil warna dari wallpaper pengguna.
    // Biasanya lebih baik dinonaktifkan jika Anda ingin branding warna yang kuat.
    dynamicColor: Boolean = false,
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