package com.cognifyteam.cognifyapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.cognifyteam.cognifyapp.data.AppContainer
import com.cognifyteam.cognifyapp.ui.auth.LoginScreen
import com.cognifyteam.cognifyapp.ui.auth.RegisterScreen


@Composable
fun AuthNavGraph(
    appContainer: AppContainer,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    navigationActions: AuthNavigationActions,
    startDestination: String = AuthDestinations.LOGIN_ROUTE,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(AuthDestinations.LOGIN_ROUTE) {

        }
        composable(AuthDestinations.REGISTER_ROUTE) {

        }
    }
}