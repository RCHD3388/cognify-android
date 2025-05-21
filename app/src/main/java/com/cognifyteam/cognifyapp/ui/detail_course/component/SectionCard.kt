package com.cognifyteam.cognifyapp.ui.detail_course.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cognifyteam.cognifyapp.ui.detail_course.screen.CourseSection

@Composable
fun SectionCard(
    section: CourseSection,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = section.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = section.duration,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                IconButton(onClick = onToggle) {
                    Icon(
                        imageVector = if (section.expanded)
                            Icons.Default.KeyboardArrowDown
                        else
                            Icons.Default.KeyboardArrowUp,
                        contentDescription = if (section.expanded) "Collapse" else "Expand"
                    )
                }
            }

            AnimatedVisibility (visible = section.expanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    section.lessons.forEach { lesson ->
                        LessonItem(
                            lessonTitle = lesson.title,
                            description = lesson.description
                        )
                    }
                }
            }
        }
    }
}