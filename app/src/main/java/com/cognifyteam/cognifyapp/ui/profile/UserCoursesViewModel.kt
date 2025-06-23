package com.cognifyteam.cognifyapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cognifyteam.cognifyapp.data.models.Course
import com.cognifyteam.cognifyapp.data.repositories.CourseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Sealed interface untuk merepresentasikan semua kemungkinan state UI
 * saat memuat daftar kursus.
 */
sealed interface UserCoursesUiState {
    /**
     * State ketika data berhasil dimuat.
     * @param courses Daftar kursus yang akan ditampilkan.
     */
    data class Success(val courses: List<Course>) : UserCoursesUiState

    /**
     * State ketika terjadi error.
     * @param message Pesan error yang bisa ditampilkan ke pengguna.
     */
    data class Error(val message: String) : UserCoursesUiState

    /**
     * State ketika data sedang dimuat dari repository.
     */
    object Loading : UserCoursesUiState
}

/**
 * ViewModel yang bertanggung jawab untuk mengambil dan menampung
 * state dari daftar kursus yang diikuti oleh pengguna.
 */
class UserCoursesViewModel(
    private val courseRepository: CourseRepository
) : ViewModel() {

    // StateFlow privat untuk menampung state, hanya bisa diubah di dalam ViewModel.
    // Diinisialisasi dengan state Loading.
    private val _uiState = MutableStateFlow<UserCoursesUiState>(UserCoursesUiState.Loading)

    // StateFlow publik yang hanya bisa dibaca (read-only) oleh UI.
    val uiState: StateFlow<UserCoursesUiState> = _uiState

    /**
     * Memulai proses untuk mengambil daftar kursus yang diikuti dari repository.
     * @param firebaseId ID dari pengguna yang kursusnya akan diambil.
     */
    fun loadEnrolledCourses(firebaseId: String) {
        viewModelScope.launch {
            // Set state menjadi Loading setiap kali fungsi ini dipanggil
            _uiState.value = UserCoursesUiState.Loading

            // Panggil repository untuk mendapatkan hasilnya
            val result = courseRepository.getEnrolledCourses(firebaseId)

            // "Buka" hasil dari repository
            result.onSuccess { courses ->
                // Jika sukses, perbarui state dengan daftar kursus
                _uiState.value = UserCoursesUiState.Success(courses)
            }.onFailure { exception ->
                // Jika gagal, perbarui state dengan pesan error
                _uiState.value = UserCoursesUiState.Error(exception.message ?: "Failed to load courses")
            }
        }
    }

    /**
     * Companion object yang menyediakan Factory untuk membuat instance ViewModel ini
     * dengan dependensi yang dibutuhkan (courseRepository).
     */
    companion object {
        fun provideFactory(
            courseRepository: CourseRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(UserCoursesViewModel::class.java)) {
                    return UserCoursesViewModel(courseRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}