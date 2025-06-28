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
import com.cognifyteam.cognifyapp.ui.course.addcourse.SectionState
import com.cognifyteam.cognifyapp.ui.course.addcourse.MaterialState

/**
 * Sealed interface untuk merepresentasikan semua kemungkinan state UI
 * saat memuat daftar course yang diikuti (enrolled) oleh pengguna.
 */
sealed interface CourseUiState {
    data class Success(val courses: List<Course>) : CourseUiState
    data class Error(val message: String) : CourseUiState
    object Loading : CourseUiState
}

class CourseViewModel(
    private val courseRepository: CourseRepository
) : ViewModel() {

    // --- LOGIKA YANG SUDAH ADA (TIDAK DIUBAH) ---
    private val _uiState = MutableStateFlow<CourseUiState>(CourseUiState.Loading)
    val uiState: StateFlow<CourseUiState> = _uiState

    private val _event = MutableSharedFlow<String>()
    val event: SharedFlow<String> = _event

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

    fun loadCreateCourses(firebaseId : String) {
        viewModelScope.launch {
            _uiState.value = CourseUiState.Loading
            val result = courseRepository.getUserCreatedCourses(firebaseId)
            Log.d("CourseViewModel", "Result: $result")
            result.onSuccess { courses ->
                _uiState.value = CourseUiState.Success(courses)
            }.onFailure { exception ->
                _uiState.value = CourseUiState.Error("PESAN ERRORNYA " + exception.message ?: "Failed to load courses")
            }
        }
    }

    fun resetCreateCourseState() {
        _createCourseState.value = CreateCourseState.Idle
    }
    // --- AKHIR DARI LOGIKA YANG SUDAH ADA ---


    // --- LOGIKA BARU UNTUK SECTIONS DAN MATERIALS ---

    private val _sections = MutableStateFlow<List<SectionState>>(emptyList())
    val sections: StateFlow<List<SectionState>> = _sections.asStateFlow()

    fun addSection(title: String) {
        val newSection = SectionState(title = title)
        _sections.value = _sections.value + newSection
    }

    fun removeSection(index: Int) {
        _sections.value = _sections.value.toMutableList().also { it.removeAt(index) }
    }

    fun addMaterialToSection(sectionIndex: Int, material: MaterialState) {
        val currentSections = _sections.value.toMutableList()
        if (sectionIndex >= 0 && sectionIndex < currentSections.size) {
            currentSections[sectionIndex].materials.add(material)
            _sections.value = currentSections
        }
    }

    fun removeMaterialFromSection(sectionIndex: Int, materialIndex: Int) {
        val currentSections = _sections.value.toMutableList()
        if (sectionIndex >= 0 && sectionIndex < currentSections.size) {
            currentSections[sectionIndex].materials.removeAt(materialIndex)
            _sections.value = currentSections
        }
    }

    /**
     * Fungsi baru untuk membuat course beserta semua section dan materialnya.
     */
    fun createCourseWithContents(
        course_name: String,
        course_description: String,
        course_owner: String,
        course_price:Int,
        category_id: String,
        thumbnailFile: File
    ) {
        viewModelScope.launch {
            // Validasi di ViewModel sebelum mengirim ke repository
            if (_sections.value.isEmpty()) {
                _createCourseState.value = CreateCourseState.Error("Course must have at least one section.")
                return@launch
            }
            if (_sections.value.any { it.materials.isEmpty() }) {
                _createCourseState.value = CreateCourseState.Error("Each section must have at least one material.")
                return@launch
            }

            _createCourseState.value = CreateCourseState.Loading

            // Anda perlu memanggil fungsi repository baru yang mendukung pengiriman data bersarang
            // Contoh: val result = courseRepository.createCourseWithContents(...)
            // Untuk saat ini, kita gunakan yang sudah ada dan tambahkan data section.

            // Simulasi sukses untuk pengembangan UI
            kotlinx.coroutines.delay(2000)
            _createCourseState.value = CreateCourseState.Success("Course with content created successfully! (Simulated)")

            /*
            // Contoh implementasi nyata dengan repository
            val result = courseRepository.createCourseWithContents(
                courseName = course_name,
                courseDescription = course_description,
                courseOwner = course_owner,
                price = course_price,
                categoryId = category_id,
                thumbnail = thumbnailFile,
                sections = _sections.value // Kirim data section
            )

            result.onSuccess {
                _createCourseState.value = CreateCourseState.Success("Successfully created course and content!")
            }.onFailure { exception ->
                _createCourseState.value = CreateCourseState.Error(exception.message ?: "Failed to create course with content")
            }
            */
        }
    }

    fun updateMaterialInSection(sectionIndex: Int, materialIndex: Int, updatedMaterial: MaterialState) {
        val currentSections = _sections.value.toMutableList()
        if (sectionIndex >= 0 && sectionIndex < currentSections.size) {
            val materials = currentSections[sectionIndex].materials
            if (materialIndex >= 0 && materialIndex < materials.size) {
                materials[materialIndex] = updatedMaterial
                _sections.value = currentSections
            }
        }
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
