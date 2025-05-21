package com.cognifyteam.cognifyapp.ui.detail_course.component

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun RatingBar(rating: Float) {
    Row {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating.toInt()) Icons.Default.Star else Icons.Default.Face,
                contentDescription = null,
                tint = Color.Yellow
            )
        }
    }
}