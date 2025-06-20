package com.cognifyteam.cognifyapp.ui.profile

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cognifyteam.cognifyapp.ui.course.CourseScreen


@Composable
fun ProfileNavigation(navController: NavController) {
    NavHost(
        navController = navController as NavHostController,
        startDestination = "course_list"
    ) {
        composable("course_list") {
            ProfilePage(navController = navController)
        }
        composable("course_details/{courseId}") { backStackEntry ->
            CourseScreen()
        }
    }
}