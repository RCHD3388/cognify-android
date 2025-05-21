package com.cognifyteam.cognifyapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppBottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : AppBottomNavItem("home", "Home", Icons.Default.Home)
    object Profile : AppBottomNavItem("profile", "Profile", Icons.Default.AccountCircle)
    object Settings : AppBottomNavItem("settings", "Settings", Icons.Default.Settings)
}
