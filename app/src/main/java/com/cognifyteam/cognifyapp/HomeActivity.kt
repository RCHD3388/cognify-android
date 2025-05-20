package com.cognifyteam.cognifyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import com.cognifyteam.cognifyapp.CognifyApplication
import com.cognifyteam.cognifyapp.ui.home.HomeScreen
import com.cognifyteam.cognifyapp.ui.theme.CognifyApplicationTheme

class HomeActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Jika kamu menggunakan DI atau container dari Application class
        val appContainer = (application as CognifyApplication).container

        setContent {
            CognifyApplicationTheme {
                // Di sini kamu bisa panggil screen utama Home
                HomeScreen(appContainer)
            }
        }
    }
}