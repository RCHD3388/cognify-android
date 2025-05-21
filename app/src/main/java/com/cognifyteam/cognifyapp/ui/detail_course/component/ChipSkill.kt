package com.cognifyteam.cognifyapp.ui.detail_course.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChipSkill(skill: String) {
    Surface (
        shape = RoundedCornerShape(8.dp),
        color = Color.LightGray,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = skill,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 12.sp
        )
    }
}