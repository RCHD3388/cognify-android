package com.cognifyteam.cognifyapp.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.*

@Composable
fun HomeScreen(
    // Jika kamu menggunakan Navigation, tambahkan parameter navController
    // navController: NavHostController? = null
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    // Untuk menyimpan nama/email pengguna secara lokal di Composable
    val userEmail by remember { mutableStateOf(currentUser?.email ?: "Tidak ada pengguna") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to Home", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Selamat datang, $userEmail",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Aksi logout
            auth.signOut()
            // Di sini kamu bisa navigasi ke halaman login
        }) {
            Text("Logout")
        }
    }
}