package com.cognifyteam.cognifyapp.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

@Composable
fun InitialAvatar(name: String, modifier: Modifier = Modifier) {
    val initial = name.firstOrNull()?.toString()?.uppercase() ?: ""
    val colors = listOf(
        Color(0xFFF44336), // Merah
        Color(0xFFE91E63), // Merah Muda
        Color(0xFF9C27B0), // Ungu
        Color(0xFF673AB7), // Ungu Tua
        Color(0xFF3F51B5), // Indigo
        Color(0xFF2196F3), // Biru
        Color(0xFF03A9F4), // Biru Muda
        Color(0xFF00BCD4), // Cyan
        Color(0xFF009688), // Teal
        Color(0xFF4CAF50), // Hijau
        Color(0xFF8BC34A), // Hijau Muda
        Color(0xFFCDDC39), // Limau
        Color(0xFFFFEB3B), // Kuning
        Color(0xFFFFC107), // Amber
        Color(0xFFFF9800), // Oranye
        Color(0xFFD2691E), // Cokelat
        Color(0xFF607D8B)  // Biru Keabu-abuan
    )
    val backgroundColor = colors[initial.hashCode() % colors.size]

    Box(
        modifier = modifier
            .background(backgroundColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            color = Color.White,
            fontSize = 48.sp, // Sesuaikan ukuran font agar pas
            fontWeight = FontWeight.Bold
        )
    }
}