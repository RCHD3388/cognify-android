package com.cognifyteam.cognifyapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cognifyteam.cognifyapp.CognifyApplication
import com.cognifyteam.cognifyapp.data.repository.UserRepository
import com.cognifyteam.cognifyapp.data.repository.impl.UserRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    data class Success(val email: String? = null) : AuthUiState
    object Loading : AuthUiState
    data class Error(val message: String) : AuthUiState
    object Unauthenticated : AuthUiState
}

class AuthViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    private val auth by lazy { FirebaseAuth.getInstance() }

    private val _uiState = MutableLiveData<AuthUiState>()
    val uiState: LiveData<AuthUiState> = _uiState

    init {
        _uiState.value = AuthUiState.Unauthenticated
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _uiState.value = AuthUiState.Success(task.result?.user?.email)
                    } else {
                        _uiState.value = AuthUiState.Error(task.exception?.message ?: "Register failed")
                    }
                }

        }

    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _uiState.value = AuthUiState.Success(task.result?.user?.email)
                    } else {
                        _uiState.value = AuthUiState.Error(task.exception?.message ?: "Login failed")
                    }
                }
        }
    }

    fun logout() {
        auth.signOut()
        _uiState.value = AuthUiState.Unauthenticated
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.currentUser?.let { currentUser ->
            // If user is already signed in with email/password, try to link with Google
            currentUser.linkWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Successfully linked Google account with email/password account
                        _uiState.value = AuthUiState.Success("Google account linked successfully!")
                    } else {
                        // If linking fails (e.g., Google account is already linked), display an error message
                        _uiState.value = AuthUiState.Error(task.exception?.message ?: "Failed to link Google account")
                    }
                }
        } ?: run {
            // If the user is not signed in with email/password, sign them in with Google
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _uiState.value = AuthUiState.Success("Logged in with Google!")
                    } else {
                        _uiState.value = AuthUiState.Error(task.exception?.message ?: "Google login failed")
                    }
                }
        }
    }
}