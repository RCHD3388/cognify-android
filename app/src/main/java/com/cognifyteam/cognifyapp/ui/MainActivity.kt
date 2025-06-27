package com.cognifyteam.cognifyapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import com.cognifyteam.cognifyapp.CognifyApplication
import com.cognifyteam.cognifyapp.ui.auth.AuthToggleScreen
import com.cognifyteam.cognifyapp.ui.theme.CognifyApplicationTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val appContainer = (application as CognifyApplication).container

        // 1. Periksa status user DI SINI, HANYA SEKALI saat Activity dibuat
        val currentUser = FirebaseAuth.getInstance().currentUser
        val isUserLoggedInAndVerified = currentUser != null && currentUser.isEmailVerified

        // 2. Jika sudah login, langsung start AppActivity dan tutup MainActivity
        if (isUserLoggedInAndVerified) {
            val intent = Intent(this, AppActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish() // <-- PENTING: Tutup MainActivity agar tidak bisa kembali ke sini
            return   // <-- Hentikan eksekusi lebih lanjut
        }

        // 3. Jika belum login, baru tampilkan layar otentikasi
        setContent {
            CognifyApplicationTheme {
                // AuthToggleScreen sekarang tidak perlu lagi memeriksa status login
                AuthToggleScreen(appContainer)
            }
        }
    }
}

