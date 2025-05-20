package com.cognifyteam.cognifyapp.ui.auth

import android.content.Intent
import android.util.Log
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
import com.cognifyteam.cognifyapp.data.AppContainer
import com.google.firebase.auth.FirebaseAuth

enum class AuthScreen {
    LOGIN, REGISTER
}

@Composable
fun AuthToggleScreen(appContainer: AppContainer) {
    var currentScreen by remember { mutableStateOf(AuthScreen.LOGIN) }
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.provideFactory(
            postsRepository = appContainer.authRepository
        )
    )
    val context = LocalContext.current

    val user = FirebaseAuth.getInstance().currentUser
    if(user != null && user.isEmailVerified){
        val intent = Intent(context, HomeActivity::class.java)
        context.startActivity(intent)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        when (currentScreen) {
            AuthScreen.LOGIN -> LoginScreen(
                appContainer = appContainer,
                onNavigateToRegister = { currentScreen = AuthScreen.REGISTER },
                onLoginSuccess = {
                    val intent = Intent(context, HomeActivity::class.java)
                    context.startActivity(intent)
                },
            )

            AuthScreen.REGISTER -> RegisterScreen(
                appContainer = appContainer,
                onNavigateToLogin = { currentScreen = AuthScreen.LOGIN },
                onRegisterSuccess = { currentScreen = AuthScreen.LOGIN
                    viewModel.resetUiState()
                }
            )
        }
    }
}