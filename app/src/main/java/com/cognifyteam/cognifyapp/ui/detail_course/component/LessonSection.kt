package com.cognifyteam.cognifyapp.ui.detail_course.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cognifyteam.cognifyapp.ui.detail_course.screen.CourseSection

@Composable
fun LessonsSection(sections: MutableList<CourseSection>) {
    // Section Title
    Text(
        text = "Lessons (32)",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 16.dp)
    )

    sections.forEach { section ->
        SectionCard (
            section = section,
            onToggle = {
                section.expanded = !section.expanded
            }
        )
    }
}