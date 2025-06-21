package com.cognifyteam.cognifyapp.ui.profile

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cognifyteam.cognifyapp.data.AppContainer
import com.cognifyteam.cognifyapp.ui.course.CourseScreen


@Composable
fun ProfileNavigation(appContainer: AppContainer, navController: NavController) {
    NavHost(
        navController = navController as NavHostController,
        startDestination = "course_list"
    ) {
        composable("course_list") {
            ProfilePage( appContainer = appContainer , navController = navController, "IlFBr2NNlQS3XWuZRU6BALaOuXP2")
        }
        composable("course_details/{courseId}") { backStackEntry ->
            CourseScreen()
        }
    }
}