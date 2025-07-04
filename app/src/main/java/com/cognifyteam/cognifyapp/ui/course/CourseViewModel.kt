package com.cognifyteam.cognifyapp.ui.course

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cognifyteam.cognifyapp.data.models.Course
import com.cognifyteam.cognifyapp.data.models.CreateMultipleSectionsRequest
import com.cognifyteam.cognifyapp.data.repositories.CourseRepository
import com.cognifyteam.cognifyapp.data.sources.remote.CreatePaymentRequest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import com.cognifyteam.cognifyapp.ui.course.addcourse.SectionState
import com.cognifyteam.cognifyapp.ui.course.addcourse.MaterialState

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

sealed interface CreateCourseWithContentState {
    object Idle : CreateCourseWithContentState
    object Loading : CreateCourseWithContentState
    data class Success(val message: String) : CreateCourseWithContentState
    data class Error(val message: String) : CreateCourseWithContentState
}
sealed interface SectionUiState {
    data class Success(val sections: List<com.cognifyteam.cognifyapp.data.models.Section>) : SectionUiState
    data class Error(val message: String) : SectionUiState
    object Loading : SectionUiState
}
sealed interface MaterialUiState {
    data class Success(val materials: List<com.cognifyteam.cognifyapp.data.models.Material>) : MaterialUiState
    data class Error(val message: String) : MaterialUiState
    object Loading : MaterialUiState
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

    private val _paymentToken = MutableSharedFlow<String>()
    val paymentToken: SharedFlow<String> = _paymentToken

    private val _paymentError = MutableSharedFlow<String>()
    val paymentError: SharedFlow<String> = _paymentError


    private val _materialsStateMap = MutableStateFlow<Map<String, MaterialUiState>>(emptyMap())
    val materialsStateMap: StateFlow<Map<String, MaterialUiState>> = _materialsStateMap.asStateFlow()


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
    fun loadMaterialsForSection(sectionId: String) {
        // Cek agar tidak memuat ulang data yang sudah ada atau sedang loading
        if (_materialsStateMap.value[sectionId] is MaterialUiState.Loading || _materialsStateMap.value[sectionId] is MaterialUiState.Success) {
            return
        }

        viewModelScope.launch {
            // Update state untuk section ini menjadi Loading
            _materialsStateMap.value = _materialsStateMap.value + (sectionId to MaterialUiState.Loading)

            // Panggil repository
            val result = courseRepository.getMaterialsBySectionId(sectionId)
            result.onSuccess { materialJsons ->
                val materials = materialJsons.map { com.cognifyteam.cognifyapp.data.models.Material.fromJson(it) }
                // Update state untuk section ini menjadi Success
                _materialsStateMap.value = _materialsStateMap.value + (sectionId to MaterialUiState.Success(materials))
            }.onFailure {
                // Update state untuk section ini menjadi Error
                _materialsStateMap.value = _materialsStateMap.value + (sectionId to MaterialUiState.Error(it.message ?: "Failed to load materials"))
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

    fun createCourse(
        course_name: String,
        course_description: String,
        course_owner: String,
        course_price: Int,
        category_id: String,
        thumbnailFile: File,
        course_owner_name: String
    ) {
        viewModelScope.launch {
            _createCourseState.value = CreateCourseState.Loading

            // Memanggil repository dengan signature yang sudah benar
            val result = courseRepository.createCourse(
                course_name = course_name,
                course_description = course_description,
                course_owner = course_owner,
                course_price = course_price,
                category_id = category_id,
                thumbnail = thumbnailFile,
                sectionsWithMaterials = _sections.value, // <-- Mengirim state langsung
                course_owner_name = course_owner_name
            )

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


    private val _sections = MutableStateFlow<List<SectionState>>(emptyList())
    val sections: StateFlow<List<SectionState>> = _sections.asStateFlow()

    fun addSection(title: String) {

        val newSection = SectionState(
            title = title,
        )
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

    fun createPayment(courseId: String, firebaseId: String, onResult: (snapToken: String) -> Unit) {
        viewModelScope.launch {
            val result = courseRepository.createPayment(courseId, CreatePaymentRequest(firebaseId))
            result.onSuccess { token ->
                // Jika sukses, panggil callback yang diterima dari UI dengan token yang didapat
                Log.d("CourseViewModel", "Payment token received: $token. Calling onResult.")
                onResult(token) // GANTI DARI _paymentToken.emit(token)
            }.onFailure { error ->
                // Jika gagal, kirim pesan error
                Log.e("CourseViewModel", "Payment creation failed: ${error.message}")
                _paymentError.emit(error.message ?: "Failed to create payment transaction")
            }
        }
    }
    private val _sectionUiState = MutableStateFlow<SectionUiState>(SectionUiState.Loading)
    val sectionUiState: StateFlow<SectionUiState> = _sectionUiState.asStateFlow()

    // TAMBAHKAN FUNGSI BARU UNTUK MEMUAT SECTIONS
    fun loadSections(courseId: String) {
        viewModelScope.launch {
            _sectionUiState.value = SectionUiState.Loading
            // Anda mungkin perlu menambahkan `getSectionsByCourse` ke CourseRepository jika belum ada
            val result = courseRepository.getSectionsByCourseId(courseId)
            result.onSuccess { sections ->
                _sectionUiState.value = SectionUiState.Success(sections)
            }.onFailure {
                _sectionUiState.value = SectionUiState.Error(it.message ?: "Failed to load sections")
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