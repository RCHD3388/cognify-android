package com.cognifyteam.cognifyapp.ui.course

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cognifyteam.cognifyapp.data.models.Course
import com.cognifyteam.cognifyapp.data.models.CourseJson
import com.cognifyteam.cognifyapp.data.repositories.CourseRepository

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

/**
 * Sealed interface untuk merepresentasikan semua kemungkinan state UI
 * saat memuat daftar course yang diikuti (enrolled) oleh pengguna.
 */
sealed interface CourseUiState {
    /**
     * State ketika data course berhasil dimuat.
     * @param courses Daftar course yang akan ditampilkan di UI.
     */
    data class Success(val courses: List<Course>) : CourseUiState

    /**
     * State ketika terjadi error saat mengambil data.
     * @param message Pesan error yang bisa ditampilkan ke pengguna.
     */
    data class Error(val message: String) : CourseUiState

    /**
     * State ketika data sedang dimuat dari repository, baik dari remote maupun cache.
     */
    object Loading : CourseUiState
}


class CourseViewModel(
    private val courseRepository: CourseRepository
) : ViewModel() {


    private val _uiState = MutableStateFlow<CourseUiState>(CourseUiState.Loading)

    val uiState: StateFlow<CourseUiState> = _uiState

    private val _event = MutableSharedFlow<String>()
    val event: SharedFlow<String> = _event





    // --- State untuk Membuat Course Baru ---
    // Dipisahkan agar tidak mengganggu state daftar course utama.

    sealed interface CreateCourseState {
        object Idle : CreateCourseState
        object Loading : CreateCourseState
        data class Success(val message: String) : CreateCourseState
        data class Error(val message: String) : CreateCourseState
    }

    private val _createCourseState = MutableStateFlow<CreateCourseState>(CreateCourseState.Idle)
    val createCourseState: StateFlow<CreateCourseState> = _createCourseState.asStateFlow()


    fun createCourse(courseData: CourseJson, thumbnailFile: File) {
        viewModelScope.launch {
            _createCourseState.value = CreateCourseState.Loading
            val result = courseRepository.createCourse(courseData, thumbnailFile)

            result.onSuccess { newCourse ->
                _createCourseState.value = CreateCourseState.Success("Course '${newCourse.name}' berhasil dibuat!")
            }.onFailure { exception ->
                _createCourseState.value = CreateCourseState.Error(exception.message ?: "Gagal membuat course")
            }
        }
    }

    /**
     * Mereset state pembuatan course kembali ke Idle setelah UI menanganinya.
     */
    fun resetCreateCourseState() {
        _createCourseState.value = CreateCourseState.Idle
    }


    /**
     * Companion object yang menyediakan Factory untuk membuat instance ViewModel ini
     * dengan dependensi yang dibutuhkan (courseRepository), sama seperti pada contoh Anda.
     */
    companion object {
        fun provideFactory(
            courseRepository: CourseRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(CourseViewModel::class.java)) {
                    return CourseViewModel(courseRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
