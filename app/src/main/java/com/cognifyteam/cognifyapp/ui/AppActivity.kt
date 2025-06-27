// Berkas: AppActivity.kt

package com.cognifyteam.cognifyapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cognifyteam.cognifyapp.CognifyApplication
import com.cognifyteam.cognifyapp.ui.common.UserViewModel
import com.cognifyteam.cognifyapp.ui.theme.CognifyApplicationTheme

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val appContainer = (application as CognifyApplication).container

        setContent {
            // 1. Dapatkan instance dari UserViewModel
            val userViewModel: UserViewModel = viewModel(
                factory = UserViewModel.provideFactory(appContainer.authRepository)
            )

            // 2. Amati state user dari UserViewModel
            val userState by userViewModel.userState.collectAsState()

            // 3. Gunakan LaunchedEffect untuk merespon perubahan state user.
            //    Key 'userState' memastikan blok ini hanya berjalan saat state berubah.
            LaunchedEffect(userState) {
                // Jika userState adalah null, itu berarti pengguna telah logout.
                if (userState == null) {
                    // Buat Intent untuk kembali ke MainActivity (layar login)
                    val intent = Intent(this@AppActivity, MainActivity::class.java).apply {
                        // Flags ini penting untuk membersihkan tumpukan activity
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    // Tutup AppActivity saat ini agar tidak bisa dikembalikan dengan tombol back
                    finish()
                }
            }

            CognifyApplicationTheme(dynamicColor = false) {
                // Tampilkan AppMainScreen HANYA JIKA user masih login.
                // Ini mencegah flicker atau error saat transisi logout.
                if (userState != null) {
                    AppMainScreen(appContainer)
                }
            }
        }
    }
}