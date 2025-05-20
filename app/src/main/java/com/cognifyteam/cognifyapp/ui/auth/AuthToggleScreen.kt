package com.cognifyteam.cognifyapp.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.cognifyteam.cognifyapp.data.AppContainer

enum class AuthScreen {
    LOGIN, REGISTER
}

@Composable
fun AuthToggleScreen(appContainer: AppContainer) {
    var currentScreen by remember { mutableStateOf(AuthScreen.LOGIN) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        when (currentScreen) {
            AuthScreen.LOGIN -> LoginScreen(
                appContainer = appContainer,
                onNavigateToRegister = { currentScreen = AuthScreen.REGISTER },
                onLoginSuccess = { /* Handle login success */ },
            )

            AuthScreen.REGISTER -> RegisterScreen(
                appContainer = appContainer,
                onNavigateToLogin = { currentScreen = AuthScreen.LOGIN },
                onRegisterSuccess = { /* Handle register success */ }
            )
        }
    }
}