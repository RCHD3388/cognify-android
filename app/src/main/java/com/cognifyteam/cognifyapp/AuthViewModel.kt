package com.cognifyteam.cognifyapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _status = MutableStateFlow("")
    val status = _status.asStateFlow()

    fun register(email: String, password: String) {
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    _status.value = if (task.isSuccessful) "Registered!" else task.exception?.message ?: "Register failed"
                }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    _status.value = if (task.isSuccessful) "Logged in!" else task.exception?.message ?: "Login failed"
                }
        }
    }

    fun logout() {
        auth.signOut()
        _status.value = "Logged out!"
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                _status.value = if (task.isSuccessful) "Logged in with Google!" else task.exception?.message ?: "Google login failed"
            }
    }
}