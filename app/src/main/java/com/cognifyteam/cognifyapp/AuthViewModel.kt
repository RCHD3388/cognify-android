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

        auth.currentUser?.let { currentUser ->
            // If user is already signed in with email/password, try to link with Google
            currentUser.linkWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Successfully linked Google account with email/password account
                        _status.value = "Google account linked successfully!"
                    } else {
                        // If linking fails (e.g., Google account is already linked), display an error message
                        _status.value = task.exception?.message ?: "Failed to link Google account"
                    }
                }
        } ?: run {
            // If the user is not signed in with email/password, sign them in with Google
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _status.value = "Logged in with Google!"
                    } else {
                        _status.value = task.exception?.message ?: "Google login failed"
                    }
                }
        }
    }


}