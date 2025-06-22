package com.cognifyteam.cognifyapp.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.data.repositories.auth.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

// ViewModel ini hanya punya satu tugas: menyediakan state user ke UI.
class UserViewModel(authRepository: AuthRepository) : ViewModel() {

    // Ambil flow user dari repository dan ubah menjadi StateFlow
    // yang akan diobservasi oleh Composable.
    val userState: StateFlow<User?> = authRepository.loggedInUser
        .stateIn(
            scope = viewModelScope,
            // Mulai sharing saat ada subscriber, berhenti 5 detik setelah subscriber terakhir hilang.
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null // Nilai awal saat belum ada data
        )

    // Ini adalah 'factory' untuk membuat ViewModel yang butuh parameter (seperti authRepository)
    companion object {
        fun provideFactory(
            authRepository: AuthRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return UserViewModel(authRepository) as T
            }
        }
    }
}