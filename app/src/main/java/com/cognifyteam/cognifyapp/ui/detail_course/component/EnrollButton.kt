package com.cognifyteam.cognifyapp.ui.detail_course.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EnrollButton(coursePrice: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
//        verticalAlignment = Alignment.CenterVertically
    ) {
        // Bagian Harga Kursus
        Column (
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = coursePrice,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Text(
                text = "VAT Included",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
        }

        // Tombol Enroll
        Button(
            onClick = { /* Handle enroll click */ },
            modifier = Modifier
                .weight(1f)
                .height(IntrinsicSize.Min),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "GET ENROLL",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}