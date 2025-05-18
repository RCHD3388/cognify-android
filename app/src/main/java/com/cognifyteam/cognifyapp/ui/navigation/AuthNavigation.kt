package com.cognifyteam.cognifyapp.ui.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

object AuthDestinations {
    const val LOGIN_ROUTE = "login"
    const val REGISTER_ROUTE = "register"
}

/**
 * Models the navigation actions in the app.
 */
class AuthNavigationActions(navController: NavHostController) {
    val navigateToLogin: () -> Unit = {
        navController.navigate(AuthDestinations.LOGIN_ROUTE) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }
    val navigateToRegister: () -> Unit = {
        navController.navigate(AuthDestinations.REGISTER_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}