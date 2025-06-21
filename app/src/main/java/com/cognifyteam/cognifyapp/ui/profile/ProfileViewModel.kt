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

    // Gunakan LiveData atau StateFlow untuk menampung hasil
    private val _userProfile = MutableLiveData<User?>()
    val userProfile: LiveData<User?> = _userProfile

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadProfile(firebaseId: String) {
        // Jalankan di dalam coroutine scope
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Panggil suspend fun secara langsung
                val user = repository.getProfile(firebaseId)
                if (user != null) {
                    _userProfile.value = user
                } else {
                    _error.value = "User not found"
                }
            } catch (e: Exception) {
                // Menangkap error tak terduga lainnya
                _error.value = "An unexpected error occurred: ${e.message}"
            } finally {
                _isLoading.value = false
            }
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