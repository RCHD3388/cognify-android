package com.cognifyteam.cognifyapp.ui.course

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cognifyteam.cognifyapp.data.models.Course
import com.cognifyteam.cognifyapp.data.models.CreateMultipleSectionsRequest
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

sealed interface CourseListUiState {
    data class Success(val courses: List<Course>) : CourseListUiState
    data class Error(val message: String) : CourseListUiState
    object Loading : CourseListUiState
}
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

class CourseViewModel(
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val _recentCoursesState = MutableStateFlow<CourseListUiState>(CourseListUiState.Loading)
    val recentCoursesState: StateFlow<CourseListUiState> = _recentCoursesState.asStateFlow()

    private val _allCoursesState = MutableStateFlow<CourseListUiState>(CourseListUiState.Loading)
    val allsellerCoursesState: StateFlow<CourseListUiState> = _allCoursesState.asStateFlow()

    private val _highestRatedCoursesState = MutableStateFlow<CourseListUiState>(CourseListUiState.Loading)
    val highestRatedCoursesState: StateFlow<CourseListUiState> = _highestRatedCoursesState.asStateFlow()

    // DIUBAH: State untuk daftar course yang DIBUAT
    private val _createdCoursesUiState = MutableStateFlow<CreatedCoursesUiState>(CreatedCoursesUiState.Loading)
    val createdCoursesUiState: StateFlow<CreatedCoursesUiState> = _createdCoursesUiState

    // --- STATE BARU UNTUK DETAIL COURSE ---
    private val _courseDetailState = MutableStateFlow<CourseDetailUiState>(CourseDetailUiState.Loading)
    val courseDetailState: StateFlow<CourseDetailUiState> = _courseDetailState.asStateFlow()

    private val _allCoursesList = MutableStateFlow<List<Course>>(emptyList())
    private val _event = MutableSharedFlow<String>()
    val event: SharedFlow<String> = _event

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _allCoursesUiState = MutableStateFlow<CourseListUiState>(CourseListUiState.Loading)
    val allCoursesUiState: StateFlow<CourseListUiState> = _allCoursesUiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        // Panggil pencarian awal saat ViewModel pertama kali dibuat
        searchAllCourses("")
    }

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

    fun searchAllCourses(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // Debounce
            _allCoursesUiState.value = CourseListUiState.Loading
            val result = courseRepository.getAllCourses(query.ifBlank { null })
            result.onSuccess { courses ->
                _allCoursesUiState.value = CourseListUiState.Success(courses)
            }.onFailure { e ->
                _allCoursesUiState.value = CourseListUiState.Error(e.message ?: "Failed to load courses")
            }
        }
    }

    fun loadInitialAllCourses() {
        // Panggil pencarian dengan query kosong
        searchAllCourses("")
    }

    fun loadAllHomeCourses() {
        loadCoursesByType("recent", _recentCoursesState)
        loadCoursesByType("bestseller", _allCoursesState)
        loadCoursesByType("highest_rating", _highestRatedCoursesState)
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

    fun createCourse(course_name: String, course_description: String, course_owner: String, course_price:Int, category_id: String, thumbnailFile: File, createMultipleSectionsRequest: CreateMultipleSectionsRequest, course_owner_name: String) {
        viewModelScope.launch {
            _createCourseState.value = CreateCourseState.Loading
            val result = courseRepository.createCourse(course_name, course_description, course_owner, course_price, category_id, thumbnailFile, createMultipleSectionsRequest, course_owner_name)
            result.onSuccess { newCourse ->
                _createCourseState.value = CreateCourseState.Success("Course '${newCourse.name}' berhasil dibuat!")
            }.onFailure { exception ->
                _createCourseState.value = CreateCourseState.Error(exception.message ?: "Gagal membuat course")
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        // Fungsi ini sekarang menjadi satu-satunya cara untuk memicu pencarian
        _searchQuery.value = query
        searchAllCourses(query)
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
                _createdCoursesUiState.value = CreatedCoursesUiState.Error("kimak " +exception.message ?: "Failed to load courses")
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

    private fun loadCoursesByType(type: String, stateFlow: MutableStateFlow<CourseListUiState>) {
        viewModelScope.launch {
            stateFlow.value = CourseListUiState.Loading
            courseRepository.getCourses(type)
                .onSuccess { courses -> stateFlow.value = CourseListUiState.Success(courses) }
                .onFailure { e -> stateFlow.value = CourseListUiState.Error(e.message ?: "Error") }
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