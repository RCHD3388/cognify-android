// Berkas: ui/course/CourseViewModel.kt

package com.cognifyteam.cognifyapp.ui.course

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cognifyteam.cognifyapp.data.models.Course
import com.cognifyteam.cognifyapp.data.repositories.CourseRepository

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.log

// DIUBAH: Ganti nama agar lebih spesifik
sealed interface CreatedCoursesUiState {
    data class Success(val courses: List<Course>) : CreatedCoursesUiState
    data class Error(val message: String) : CreatedCoursesUiState
    object Loading : CreatedCoursesUiState
}

// --- STATE BARU UNTUK DETAIL COURSE ---
sealed interface CourseDetailUiState {
    data class Success(val course: Course) : CourseDetailUiState
    data class Error(val message: String) : CourseDetailUiState
    object Loading : CourseDetailUiState
}


class CourseViewModel(
    private val courseRepository: CourseRepository
) : ViewModel() {

    // DIUBAH: State untuk daftar course yang DIBUAT
    private val _createdCoursesUiState = MutableStateFlow<CreatedCoursesUiState>(CreatedCoursesUiState.Loading)
    val createdCoursesUiState: StateFlow<CreatedCoursesUiState> = _createdCoursesUiState

    // --- STATE BARU UNTUK DETAIL COURSE ---
    private val _courseDetailState = MutableStateFlow<CourseDetailUiState>(CourseDetailUiState.Loading)
    val courseDetailState: StateFlow<CourseDetailUiState> = _courseDetailState.asStateFlow()

    private val _event = MutableSharedFlow<String>()
    val event: SharedFlow<String> = _event

    // --- FUNGSI BARU UNTUK MEMUAT DETAIL ---
    fun loadCourseDetails(courseId: String) {
        viewModelScope.launch {
            _courseDetailState.value = CourseDetailUiState.Loading
            val result = courseRepository.getCourseById(courseId)
            result.onSuccess { course ->
                _courseDetailState.value = CourseDetailUiState.Success(course)
            }.onFailure { exception ->
                _courseDetailState.value = CourseDetailUiState.Error(exception.message ?: "Failed to load course details")
            }
        }
    }

    // --- State untuk Membuat Course Baru ---
    sealed interface CreateCourseState {
        object Idle : CreateCourseState
        object Loading : CreateCourseState
        data class Success(val message: String) : CreateCourseState
        data class Error(val message: String) : CreateCourseState
    }

    private val _createCourseState = MutableStateFlow<CreateCourseState>(CreateCourseState.Idle)
    val createCourseState: StateFlow<CreateCourseState> = _createCourseState.asStateFlow()


    fun createCourse(course_name: String, course_description: String, course_owner: String, course_price:Int, category_id: String, thumbnailFile: File) {
        viewModelScope.launch {
            _createCourseState.value = CreateCourseState.Loading
            val result = courseRepository.createCourse(course_name, course_description, course_owner, course_price, category_id, thumbnailFile)
            result.onSuccess { newCourse ->
                _createCourseState.value = CreateCourseState.Success("Course '${newCourse.name}' berhasil dibuat!")
            }.onFailure { exception ->
                _createCourseState.value = CreateCourseState.Error(exception.message ?: "Gagal membuat course")
            }
        }
    }

    // DIUBAH: Fungsi ini sekarang memperbarui state yang benar
    fun loadCreateCourses(firebaseId : String) {
        viewModelScope.launch {
            _createdCoursesUiState.value = CreatedCoursesUiState.Loading
            val result = courseRepository.getUserCreatedCourses(firebaseId)
            Log.d("CourseViewModel", "Result: $result")
            result.onSuccess { courses ->
                _createdCoursesUiState.value = CreatedCoursesUiState.Success(courses)
            }.onFailure { exception ->
                _createdCoursesUiState.value = CreatedCoursesUiState.Error(exception.message ?: "Failed to load courses")
            }
        }
    }

    fun resetCreateCourseState() {
        _createCourseState.value = CreateCourseState.Idle
    }

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