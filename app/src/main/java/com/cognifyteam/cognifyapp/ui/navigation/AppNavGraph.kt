package com.cognifyteam.cognifyapp.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun HomeScreen(navController: NavHostController) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
        Text("Home Screen")
    }
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
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AppBottomNavItem.Home.route
    ) {
        composable(AppNavRoutes.HOME) {
            HomeScreen(navController)
        }
        composable(AppNavRoutes.PROFILE) {
            ProfileScreen(navController)
        }
        composable(AppNavRoutes.SETTINGS) {
            SettingsScreen(navController)
        }
    }
}
