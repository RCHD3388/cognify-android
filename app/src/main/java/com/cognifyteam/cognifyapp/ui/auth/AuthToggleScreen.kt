package com.cognifyteam.cognifyapp.ui.auth

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cognifyteam.cognifyapp.HomeActivity

enum class AuthScreen {
    LOGIN, REGISTER
}

@Composable
fun AuthToggleScreen() {
    var currentScreen by remember { mutableStateOf(AuthScreen.LOGIN) }
    val viewModel: AuthViewModel = viewModel()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        when (currentScreen) {
            AuthScreen.LOGIN -> LoginScreen(
                onNavigateToRegister = { currentScreen = AuthScreen.REGISTER },
                onLoginSuccess = {
                    val intent = Intent(context, HomeActivity::class.java)
                    context.startActivity(intent)
                },
            )

            AuthScreen.REGISTER -> RegisterScreen(
                onNavigateToLogin = { currentScreen = AuthScreen.LOGIN },
                onRegisterSuccess = { currentScreen = AuthScreen.LOGIN
                    viewModel.resetUiState()
                }
            )
        }
    }
}