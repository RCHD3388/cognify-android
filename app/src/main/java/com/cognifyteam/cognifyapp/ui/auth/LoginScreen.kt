// LoginScreen.kt

package com.cognifyteam.cognifyapp.ui.auth

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cognifyteam.cognifyapp.R
import com.cognifyteam.cognifyapp.data.AppContainer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    appContainer: AppContainer,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit
){
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.provideFactory(
            postsRepository = appContainer.authRepository
        )
    )

    val uiState by viewModel.uiState.observeAsState(AuthUiState.Unauthenticated)
    val context = LocalContext.current

    val onGoogleLogin = rememberGoogleSignLauncher(
        onIdTokenReceived = { idToken ->
            viewModel.firebaseAuthWithGoogle(idToken)
        },
        onError = { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    )

    LaunchedEffect(uiState) {
        when (val currentState = uiState) {
            is AuthUiState.Success -> {
                onLoginSuccess()
            }
            is AuthUiState.Error -> {
                Toast.makeText(context, currentState.message, Toast.LENGTH_SHORT).show()
                viewModel.resetUiState()
            }
            is AuthUiState.Verified -> {
                Toast.makeText(context, currentState.message, Toast.LENGTH_SHORT).show()
                viewModel.resetUiState()
            }
            AuthUiState.Loading, AuthUiState.Unauthenticated -> Unit
            is AuthUiState.RegisterSuccess -> {}
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Menggunakan warna background dari tema
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.robot),
            contentDescription = "Login Illustration",
            modifier = Modifier
                .height(120.dp)
                .padding(bottom = 16.dp)
        )
        Text(
            text = "Welcome Back 👋",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground // Menggunakan warna teks dari tema
        )

        Spacer(modifier = Modifier.height(32.dp))

        // TextField menggunakan warna default dari tema
        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Email") },
            singleLine = true,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_email_24),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_password_24),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val emailValue = email.value.trim()
                val passwordValue = password.value
                if (emailValue.isNotBlank() && passwordValue.isNotBlank()) {
                    viewModel.login(emailValue, passwordValue)
                } else {
                    Toast.makeText(context, "Email dan password harus diisi", Toast.LENGTH_SHORT).show()
                }
            },
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.primary,
                disabledContentColor = MaterialTheme.colorScheme.onPrimary
            ),
            enabled = uiState !is AuthUiState.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            when (uiState) {
                AuthUiState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                else -> {
                    Text("Login", fontSize = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "or",
            color = MaterialTheme.colorScheme.onSurfaceVariant, // Warna teks yang lebih lembut
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tombol Google menggunakan warna surface agar kontras
        Button(
            onClick = onGoogleLogin,
            shape = RoundedCornerShape(24.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.logo_google),
                contentDescription = "Google",
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Continue with Google", fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))

        TextButton(
            onClick = onNavigateToRegister,
            enabled = uiState !is AuthUiState.Loading
        ) {
            Text(
                buildAnnotatedString {
                    // Terapkan gaya dengan warna teks standar
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
                        append("Don't have an account? ") // Atau "Already have an account? "
                    }
                    // Terapkan gaya dengan warna utama untuk teks yang bisa diklik
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
                        append("Register") // Atau "Login"
                    }
                }
            )
        }
    }
}

@Composable
fun rememberGoogleSignLauncher(
    onIdTokenReceived: (String) -> Unit,
    onError: (String) -> Unit
): () -> Unit {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(Exception::class.java)
                val idToken = account?.idToken
                if (idToken != null) {
                    onIdTokenReceived(idToken)
                } else {
                    onError("Google sign-in failed: ID token is null")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Google sign-in failed")
            }
        } else {
            when {
                result.resultCode == Activity.RESULT_CANCELED -> {
                    onError("User canceled the sign-in flow")
                }
                result.data == null -> {
                    onError("No data returned from Google Sign-In")
                }
                else -> {
                    onError("Google sign-in failed with code: ${result.resultCode}")
                }
            }
        }
    }

    return remember {
        {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(context, gso)

            googleSignInClient.signOut().addOnCompleteListener { task ->
                launcher.launch(googleSignInClient.signInIntent)
            }
        }
    }
}