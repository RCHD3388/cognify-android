package com.cognifyteam.cognifyapp.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cognifyteam.cognifyapp.data.repositories.auth.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.math.log

sealed interface AuthUiState {
    data class Success(val email: String? = null) : AuthUiState
    object Loading : AuthUiState
    data class Error(val message: String) : AuthUiState
    data class Verified(val message: String) : AuthUiState
    data class RegisterSuccess(val message: String) : AuthUiState
    object Unauthenticated : AuthUiState
}

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val auth by lazy { FirebaseAuth.getInstance() }

    private val _uiState = MutableLiveData<AuthUiState>()
    val uiState: LiveData<AuthUiState> = _uiState

    init {
        val currentUser = auth.currentUser
        // UBAH LOGIKA DI SINI:
        // User dianggap login HANYA JIKA dia ada DAN emailnya sudah terverifikasi.
        if (currentUser != null && currentUser.isEmailVerified) {
            _uiState.value = AuthUiState.Success(currentUser.email)
        } else {
            // Jika user ada tapi belum verifikasi, atau tidak ada user sama sekali,
            // anggap sebagai Unauthenticated.
            _uiState.value = AuthUiState.Unauthenticated
        }
    }

    // File: ui/auth/AuthViewModel.kt

    fun register(name: String, email: String, password: String, role: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                // 1. Buat akun di Firebase
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val user = authResult.user

                user?.let { firebaseUser ->
                    // 2. Daftarkan di backend Anda (melalui repository)
                    // Repository sekarang hanya akan memanggil API, tidak ke Room.
                    authRepository.register(firebaseUser.uid, name, email, role)

                    // 3. Kirim email verifikasi
                    firebaseUser.sendEmailVerification().await()

                    // 4. PENTING: Logout agar user tidak langsung masuk
                    auth.signOut()

                    // 5. Update UI untuk memberitahu user
                    _uiState.value = AuthUiState.RegisterSuccess("Registrasi berhasil. Silakan cek email Anda untuk verifikasi.")
                } ?: run {
                    _uiState.value = AuthUiState.Error("User object is null after registration.")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Register failed")
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null && user.isEmailVerified) {
                            // Email sudah diverifikasi, lanjutkan ke aplikasi
                            viewModelScope.launch {
                                authRepository.login(user.uid);
                                _uiState.value = AuthUiState.Success(user.email)
                            }
                        } else {
                            // Email belum diverifikasi
                            // Kirim state Verified agar UI bisa menampilkan pesan yang tepat
                            _uiState.value = AuthUiState.Verified("Email belum diverifikasi. Silakan cek email Anda.")
                        }
                    } else {
                        // Login gagal (password salah, user tidak ada, dll)
                        _uiState.value = AuthUiState.Error(task.exception?.message ?: "Login gagal")
                    }
                }
        }
    }

    fun logout() {
        auth.signOut()
        _uiState.value = AuthUiState.Unauthenticated
    }

    fun resetUiState() {
        _uiState.value = AuthUiState.Unauthenticated
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val credential = GoogleAuthProvider.getCredential(idToken, null)

            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = task.result?.user
                        val isNewUser = task.result?.additionalUserInfo?.isNewUser // <-- PENGECEKAN UTAMA

                        user?.let { firebaseUser ->
                            val displayName = firebaseUser.displayName ?: "Nama tidak tersedia"
                            val email = firebaseUser.email ?: "Email tidak tersedia"
                            val uid = firebaseUser.uid

                            if (isNewUser == true) {
                                // 🔵 REGISTRASI
                                viewModelScope.launch {
                                    authRepository.register(uid, displayName, email, "user")
                                    _uiState.value = AuthUiState.Success(
                                        email = email,
                                    )
                                }
                            }else{
                                // 🔵 LOGIN
                                viewModelScope.launch {
                                    authRepository.login(uid);
                                    _uiState.value = AuthUiState.Success(
                                        email = email,
                                    )
                                }
                            }
                        } ?: run {
                            _uiState.value = AuthUiState.Error("User null setelah login Google")
                        }

                    } else {
                        val errorMessage = task.exception?.message ?: "Login gagal"
                        Log.e("GoogleAuth", "Error: $errorMessage")
                        _uiState.value = AuthUiState.Error(errorMessage)
                    }
                }
        }
    }

    companion object {
        fun provideFactory(
            postsRepository: AuthRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(postsRepository) as T
            }
        }
    }
}