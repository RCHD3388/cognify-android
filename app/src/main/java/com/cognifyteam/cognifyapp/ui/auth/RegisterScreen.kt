package com.cognifyteam.cognifyapp.ui.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.provideFactory(
            postsRepository = appContainer.authRepository
        )
    )

    val uiState by viewModel.uiState.observeAsState()

    val context = LocalContext.current

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Success -> {
                onRegisterSuccess()
                Toast.makeText(context, "Registration successful!", Toast.LENGTH_LONG).show()
            }
            is AuthUiState.Error -> {
                Toast.makeText(
                    context,
                    (uiState as AuthUiState.Error).message,
                    Toast.LENGTH_LONG
                ).show()
            }
            AuthUiState.Loading -> {}
            AuthUiState.Unauthenticated -> {}
            null -> {}
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
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
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(32.dp))

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
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF1F2343),
                unfocusedBorderColor = Color.Gray,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedLabelColor = Color(0xFF1F2343)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

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
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF1F2343),
                unfocusedBorderColor = Color.Gray,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedLabelColor = Color(0xFF1F2343)
            ),
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
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF1F2343),
                unfocusedBorderColor = Color.Gray,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedLabelColor = Color(0xFF1F2343)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

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
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF1F2343),
                unfocusedBorderColor = Color.Gray,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedLabelColor = Color(0xFF1F2343)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (password.value == confirm_password.value) {
                    viewModel.register(name.value, email.value, password.value)
                } else {
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            },
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1F2343),
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            when (uiState) {
                AuthUiState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
                else -> Text("Register", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        TextButton(onClick = onNavigateToLogin) {
            Text(
                buildAnnotatedString {
                    append("Already have an account? ")
                    withStyle(SpanStyle(color = Color(0xFF1F2343), fontWeight = FontWeight.Bold)) {
                        append("Login")
                    }
                }
            )
        }
    }
}