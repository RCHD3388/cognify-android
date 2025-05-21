package com.cognifyteam.cognifyapp.ui.detail_course.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.cognifyteam.cognifyapp.ui.detail_course.component.CourseHeader
import com.cognifyteam.cognifyapp.ui.detail_course.component.EnrollButton
import com.cognifyteam.cognifyapp.ui.detail_course.component.LessonsSection
import com.cognifyteam.cognifyapp.ui.detail_course.component.rememberCourseSections

data class Lesson(
    val title: String,
    val description: String
)

data class CourseSection(
    val title: String,
    val duration: String,
    val lessons: List<Lesson>,
    val initialExpanded: Boolean = false
) {
    var expanded by mutableStateOf(initialExpanded)
}

@Composable
fun CourseLessonsScreen(navController: NavHostController) {
    val sections = rememberCourseSections()

    Column(modifier = Modifier.fillMaxSize()) {

        // Header (Fix)
        CourseHeader()

        // Scrollable Content
        Surface(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                item {
                    LessonsSection(sections = sections)
                }
            }
        }

        // Footer (Fix)
        EnrollButton(coursePrice = "$95")
    }
}