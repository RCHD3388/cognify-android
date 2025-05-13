package com.cognifyteam.cognifyapp.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

enum class AuthScreen {
    LOGIN, REGISTER
}

@Composable
fun AuthToggleScreen() {
    var currentScreen by remember { mutableStateOf(AuthScreen.LOGIN) }

    when (currentScreen) {
        AuthScreen.LOGIN -> LoginScreen(
            onNavigateToRegister = { currentScreen = AuthScreen.REGISTER },
            onLoginSuccess = { /* Handle login success */ }
        )
        AuthScreen.REGISTER -> RegisterScreen(
            onNavigateToLogin = { currentScreen = AuthScreen.LOGIN },
            onRegisterSuccess = { /* Handle register success */ }
        )
    }
}