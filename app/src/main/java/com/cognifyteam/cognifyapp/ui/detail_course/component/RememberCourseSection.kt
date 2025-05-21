package com.cognifyteam.cognifyapp.ui.detail_course.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.cognifyteam.cognifyapp.ui.detail_course.screen.CourseSection
import com.cognifyteam.cognifyapp.ui.detail_course.screen.Lesson

@Composable
fun rememberCourseSections(): MutableList<CourseSection> {
    return remember {
        mutableStateListOf(
            CourseSection(
                title = "Section 1 - Introduction",
                duration = "10 Min",
                lessons = listOf(
                    Lesson("Lesson 1: What is UI/UX?", "Intro to UI/UX design"),
                    Lesson("Lesson 2: Design Principles", "Learn key principles")
                )
            ),
            CourseSection(
                title = "Section 2 - App Design Process",
                duration = "15 Min",
                lessons = listOf(
                    Lesson("Lesson 3: App Design Process", "Step-by-step guide"),
                    Lesson("Lesson 4: Wireframing", "Create your first wireframe")
                )
            )
        )
    }
}