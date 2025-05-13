package com.cognifyteam.cognifyapp.ui

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.cognifyteam.cognifyapp.data.AppContainer
import com.cognifyteam.cognifyapp.ui.navigation.AuthNavGraph
import com.cognifyteam.cognifyapp.ui.navigation.AuthNavigationActions
import com.cognifyteam.cognifyapp.ui.theme.CognifyApplicationTheme

@Composable
fun CognifyApp (
    appContainer: AppContainer,
    widthSizeClass: WindowWidthSizeClass
) {
    CognifyApplicationTheme {
        val navController = rememberNavController()
        val navigationActions = remember(navController) {
            AuthNavigationActions(navController)
        }

        AuthNavGraph(
            appContainer = appContainer,
            navController = navController,
            navigationActions = navigationActions
        )
    }
}