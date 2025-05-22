package com.cognifyteam.cognifyapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import com.cognifyteam.cognifyapp.CognifyApplication
import com.cognifyteam.cognifyapp.ui.auth.AuthToggleScreen
import com.cognifyteam.cognifyapp.ui.theme.CognifyApplicationTheme

class AppActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val appContainer = (application as CognifyApplication).container
        setContent {
            CognifyApplicationTheme(dynamicColor = false) {
                AppMainScreen(appContainer)
            }
        }
    }
}