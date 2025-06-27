package com.cognifyteam.cognifyapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.cognifyteam.cognifyapp.R

sealed class AppBottomNavItem(val route: String, val title: String, val icon: Int) {
    object Home : AppBottomNavItem("home", "Home", R.drawable.bnav_home)
    object Profile : AppBottomNavItem("profile", "Profile", R.drawable.bnav_profile)
    object Smart : AppBottomNavItem("course_details/CR001", "Smart", R.drawable.bnav_smart)
//    object Smart : AppBottomNavItem("smart", "Smart", R.drawable.bnav_smart)
}
