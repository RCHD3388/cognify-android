package com.cognifyteam.cognifyapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    data class Success(val email: String? = null) : AuthUiState
    object Loading : AuthUiState
    data class Error(val message: String) : AuthUiState
    object Unauthenticated : AuthUiState
}

class AuthViewModel : ViewModel() {
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
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _uiState.value = AuthUiState.Success(task.result?.user?.email)
                    } else {
                        _uiState.value = AuthUiState.Error(task.exception?.message ?: "Google login failed")
                    }
                }
        }
    }
}