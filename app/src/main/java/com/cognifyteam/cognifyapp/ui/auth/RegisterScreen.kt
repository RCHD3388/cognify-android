package com.cognifyteam.cognifyapp.ui.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
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
    val roles = listOf("user", "instructor")
    val selectedRole = remember { mutableStateOf(roles[0]) }

    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.provideFactory(
            postsRepository = appContainer.authRepository
        )
    )

    val uiState by viewModel.uiState.observeAsState()

    val context = LocalContext.current

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Success -> {}
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
            is AuthUiState.Verified -> {}
            is AuthUiState.RegisterSuccess -> {
                onRegisterSuccess()
                Toast.makeText(context, "Register Success, Silahkan Cek Email Anda", Toast.LENGTH_LONG).show()
            }
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

        Text(
            text = "Register as:",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 4.dp),
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            roles.forEach { role ->
                Row(
                    modifier = Modifier
                        .selectable(
                            selected = (role == selectedRole.value),
                            onClick = { selectedRole.value = role },
                            role = Role.RadioButton
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (role == selectedRole.value),
                        onClick = null,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color(0xFF1F2343)
                        )
                    )
                    Text(
                        text = role.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                        modifier = Modifier.padding(start = 8.dp),
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }
        }

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
                    viewModel.register(
                        name = name.value,
                        email = email.value,
                        password = password.value,
                        role = selectedRole.value // Kirim role yang dipilih
                    )
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