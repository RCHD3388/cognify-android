package com.cognifyteam.cognifyapp.ui.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cognifyteam.cognifyapp.data.AppContainer
import com.cognifyteam.cognifyapp.ui.learningpath.screen.MainLearningPathScreen

@Composable
fun HomeScreen(navController: NavHostController) {
    com.cognifyteam.cognifyapp.ui.home.HomeScreen()
}

@Composable
fun ProfileScreen(navController: NavHostController) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
        Text("Profile Screen")
    }
}

@Composable
fun SettingsScreen(navController: NavHostController) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
        Text("Settings Screen")
    }
}


object AppNavRoutes {
    const val HOME = "home"
    const val SMART = "smart"
    const val PROFILE = "profile"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    appContainer: AppContainer
) {
    NavHost(
        navController = navController,
        startDestination = AppBottomNavItem.Home.route
    ) {
        composable(AppNavRoutes.HOME) {
            HomeScreen(navController)
        }
        composable(AppNavRoutes.SMART) {
            MainLearningPathScreen()
        }
        composable(AppNavRoutes.PROFILE) {
            ProfileScreen(navController)
        }
    }
}
