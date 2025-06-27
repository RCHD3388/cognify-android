package com.cognifyteam.cognifyapp.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.data.repositories.auth.AuthRepository
import com.cognifyteam.cognifyapp.data.repositories.profile.ProfileRepository
import com.cognifyteam.cognifyapp.ui.auth.AuthViewModel
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _userProfile = MutableLiveData<User?>()
    val userProfile: LiveData<User?> = _userProfile

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadProfile(firebaseId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            // 1. Panggil repository dan simpan "kotak hadiahnya" (Result)
            val result = repository.getProfile(firebaseId)

            // 2. "Buka kotak" menggunakan onSuccess dan onFailure
            result.onSuccess { user ->
                // Blok ini hanya berjalan jika hasilnya SUKSES.
                // Variabel 'user' di sini adalah objek User asli.
                _userProfile.value = user
            }.onFailure { exception ->
                // Blok ini hanya berjalan jika hasilnya GAGAL.
                // Variabel 'exception' berisi detail error.
                _error.value = exception.message ?: "An unknown error occurred"
            }

            // 3. Selalu set isLoading menjadi false di akhir
            _isLoading.value = false
        }
    }

    companion object {
        fun provideFactory(
            postsRepository: ProfileRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(postsRepository) as T
            }
        }
    }
}