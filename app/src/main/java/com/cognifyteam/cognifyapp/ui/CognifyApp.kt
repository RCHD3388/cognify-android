package com.cognifyteam.cognifyapp.ui

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.cognifyteam.cognifyapp.data.AppContainer
import com.cognifyteam.cognifyapp.ui.theme.CognifyApplicationTheme

@Composable
fun CognifyApp (
    appContainer: AppContainer,
    widthSizeClass: WindowWidthSizeClass
) {
    CognifyApplicationTheme {
        val navController = rememberNavController()


    }
}