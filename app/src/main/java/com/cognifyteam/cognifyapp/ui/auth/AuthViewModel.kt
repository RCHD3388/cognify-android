package com.cognifyteam.cognifyapp.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.data.repositories.auth.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
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

    fun register(name: String, email: String, password: String, role: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            var userToRollback: FirebaseUser? = null

            try {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                userToRollback = authResult.user

                userToRollback?.let { firebaseUser ->
                    val backendResult = authRepository.register(firebaseUser.uid, name, email, role)

                    if (backendResult.isSuccess) {
                        firebaseUser.sendEmailVerification().await()
                        auth.signOut()
                        _uiState.value = AuthUiState.RegisterSuccess("Registrasi berhasil. Silakan cek email Anda untuk verifikasi.")
                    } else {
                        val originalErrorMessage = backendResult.exceptionOrNull()?.message
                        Log.e("AuthViewModel", "Backend registration failed: $originalErrorMessage")

                        firebaseUser.delete().await()
                        // Pesan error jika backend gagal, bisa lebih spesifik
                        _uiState.value = AuthUiState.Error("Registrasi gagal. Tidak dapat terhubung ke server.")
                    }
                } ?: run {
                    Log.e("AuthViewModel", "Firebase user is null after creation.")
                    _uiState.value = AuthUiState.Error("Registrasi gagal, terjadi kesalahan.")
                }

                // 👇 MODIFIKASI UTAMA ADA DI BLOK CATCH INI
            } catch (e: Exception) {
                // Log error asli terlebih dahulu
                Log.e("AuthViewModel", "Registration process failed with exception: ${e.message}")

                // Coba rollback jika user sudah sempat dibuat sebelum error utama terjadi
                // Ini jarang terjadi, tapi merupakan praktik yang aman
                try {
                    userToRollback?.delete()?.await()
                } catch (deleteEx: Exception) {
                    Log.e("AuthViewModel", "Failed to cleanup user during exception handling: ${deleteEx.message}")
                }

                // Periksa jenis Exception
                when (e) {
                    is FirebaseAuthUserCollisionException -> {
                        // KASUS SPESIFIK: Email sudah ada
                        _uiState.value = AuthUiState.Error("Registrasi gagal. Email ini sudah terdaftar.")
                    }
                    else -> {
                        // KASUS UMUM: Error lain dari Firebase atau jaringan
                        // (Contoh: format email salah, password lemah, tidak ada koneksi)
                        _uiState.value = AuthUiState.Error("Registrasi gagal. Periksa kembali data Anda dan koneksi internet.")
                    }
                }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val authResult = auth.signInWithEmailAndPassword(email, password).await()

                authResult.user?.let { user ->
                    if (!user.isEmailVerified) {
                        auth.signOut()
                        _uiState.value = AuthUiState.Verified("Email belum diverifikasi. Silakan cek email Anda.")
                        return@let
                    }

                    val backendResult = authRepository.login(user.uid)

                    if (backendResult.isSuccess) {
                        _uiState.value = AuthUiState.Success(user.email)
                    } else {
                        // Backend GAGAL
                        val originalErrorMessage = backendResult.exceptionOrNull()?.message
                        Log.e("AuthViewModel", "Backend login failed: $originalErrorMessage")

                        auth.signOut()

                        // 👇 PERUBAHAN DI SINI: Pesan generik untuk kegagalan server
                        _uiState.value = AuthUiState.Error("Login gagal. Terjadi masalah pada server.")
                    }

                } ?: run {
                    Log.e("AuthViewModel", "Firebase user is null after login.")
                    // 👇 PERUBAHAN DI SINI: Pesan generik
                    _uiState.value = AuthUiState.Error("Login gagal, terjadi kesalahan.")
                }

            } catch (e: Exception) {
                // Firebase GAGAL (misal: password salah, user tidak ada)
                Log.e("AuthViewModel", "Firebase login failed: ${e.message}")

                // 👇 PERUBAHAN DI SINI: Pesan generik untuk email/password salah
                _uiState.value = AuthUiState.Error("Login gagal. Periksa kembali email dan password Anda.")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            auth.signOut() // Logout dari Firebase
            authRepository.logout() // PANGGIL INI UNTUK MEMBERSIHKAN DATA LOKAL
            _uiState.value = AuthUiState.Unauthenticated
        }
    }

    fun resetUiState() {
        // Pastikan kita tidak mereset jika sedang loading
        if (_uiState.value !is AuthUiState.Loading) {
            _uiState.value = AuthUiState.Unauthenticated
        }
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = auth.signInWithCredential(credential).await()

                authResult.user?.let { user ->
                    val isNewUser = authResult.additionalUserInfo?.isNewUser
                    val displayName = user.displayName ?: "Pengguna Baru"
                    val email = user.email
                    if (email == null) {
                        Log.e("GoogleAuth", "Email from Google is null.")
                        user.delete().await()
                        // 👇 PERUBAHAN DI SINI: Pesan generik
                        _uiState.value = AuthUiState.Error("Login gagal. Email tidak ditemukan pada akun Google Anda.")
                        return@let
                    }

                    val uid = user.uid
                    val backendResult: Result<User>

                    if (isNewUser == true) {
                        backendResult = authRepository.register(uid, displayName, email, "user")
                    } else {
                        backendResult = authRepository.login(uid)
                    }

                    if (backendResult.isSuccess) {
                        _uiState.value = AuthUiState.Success(email = email)
                    } else {
                        // Backend GAGAL
                        val originalErrorMessage = backendResult.exceptionOrNull()?.message
                        Log.e("GoogleAuth", "Backend sync failed: $originalErrorMessage")

                        if (isNewUser == true) user.delete().await() else auth.signOut()

                        // 👇 PERUBAHAN DI SINI: Pesan generik
                        _uiState.value = AuthUiState.Error("Login gagal. Terjadi masalah pada server.")
                    }

                } ?: run {
                    Log.e("GoogleAuth", "Firebase user is null after Google Sign-In.")
                    // 👇 PERUBAHAN DI SINI: Pesan generik
                    _uiState.value = AuthUiState.Error("Login dengan Google gagal.")
                }

            } catch (e: Exception) {
                Log.e("GoogleAuth", "Google sign-in failed: ${e.message}")
                // 👇 PERUBAHAN DI SINI: Pesan generik
                _uiState.value = AuthUiState.Error("Login dengan Google gagal. Silakan coba lagi.")
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