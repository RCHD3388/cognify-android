// RegisterScreen.kt

package com.cognifyteam.cognifyapp.ui.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cognifyteam.cognifyapp.R
import com.cognifyteam.cognifyapp.data.AppContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    appContainer: AppContainer,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    onRegisterSuccess: () -> Unit
){
    val name = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirm_password = remember { mutableStateOf("") }
    val roles = listOf("user")
    val selectedRole = remember { mutableStateOf(roles[0]) }

    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.provideFactory(
            postsRepository = appContainer.authRepository
        )
    )

    val uiState by viewModel.uiState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        when (val currentState = uiState) {
            is AuthUiState.Error -> {
                Toast.makeText(context, currentState.message, Toast.LENGTH_LONG).show()
                viewModel.resetUiState()
            }
            is AuthUiState.RegisterSuccess -> {
                Toast.makeText(context, currentState.message, Toast.LENGTH_LONG).show()
                onRegisterSuccess()
            }
            else -> Unit
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
            contentDescription = "Register Illustration",
            modifier = Modifier
                .height(120.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Create Account ✨",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground // Menggunakan warna teks dari tema
        )

        Spacer(modifier = Modifier.height(32.dp))

        // TextField menggunakan warna default dari tema, ini lebih baik
        OutlinedTextField(
            value = name.value,
            onValueChange = { name.value = it },
            label = { Text("Name") },
            singleLine = true,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_email_24),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            },
            // Menghapus `colors` akan membuat TextField otomatis menggunakan warna tema
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

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

        Spacer(modifier = Modifier.height(12.dp))

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

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirm_password.value,
            onValueChange = { confirm_password.value = it },
            label = { Text("Confirm Password") },
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

        Spacer(modifier = Modifier.height(20.dp))

        // Tombol menggunakan warna primary dari tema
        Button(
            onClick = {
                val nameValue = name.value.trim()
                val emailValue = email.value.trim()
                val passwordValue = password.value
                val confirmPasswordValue = confirm_password.value

                if (nameValue.isBlank() || emailValue.isBlank() || passwordValue.isBlank() || confirmPasswordValue.isBlank()) {
                    Toast.makeText(context, "Semua field wajib diisi", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
                    Toast.makeText(context, "Format email tidak valid", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (passwordValue.length < 6) {
                    Toast.makeText(context, "Password minimal harus 6 karakter", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (passwordValue != confirmPasswordValue) {
                    Toast.makeText(context, "Password dan konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                viewModel.register(
                    name = nameValue,
                    email = emailValue,
                    password = passwordValue,
                    role = selectedRole.value
                )
            },
            shape = RoundedCornerShape(24.dp),
            // Menghapus `colors` akan membuat Button otomatis menggunakan warna primary
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            when (uiState) {
                AuthUiState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary // Warna progress di atas tombol primary
                )
                else -> Text("Register", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(
            onClick = onNavigateToLogin,
            enabled = uiState !is AuthUiState.Loading
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
                        append("Already have an account? ")
                    }
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
                        append("Login")
                    }
                }
            )
        }
    }
}