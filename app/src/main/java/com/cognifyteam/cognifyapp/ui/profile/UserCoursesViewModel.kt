package com.cognifyteam.cognifyapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cognifyteam.cognifyapp.data.models.Course
import com.cognifyteam.cognifyapp.data.repositories.CourseRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
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
@OptIn(FlowPreview::class) // Diperlukan untuk menggunakan .debounce()
class UserCoursesViewModel(
    private val courseRepository: CourseRepository
) : ViewModel() {

    // StateFlow untuk menampung query pencarian dari UI
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // StateFlow utama untuk UI, sekarang didasarkan pada perubahan searchQuery
    private val _uiState = MutableStateFlow<UserCoursesUiState>(UserCoursesUiState.Loading)
    val uiState: StateFlow<UserCoursesUiState> = _uiState.asStateFlow()

    // ID user yang datanya sedang ditampilkan
    private var currentFirebaseId: String? = null

    /**
     * Fungsi inisialisasi yang dipanggil sekali oleh UI untuk memulai pemantauan.
     */
    fun initialize(firebaseId: String) {
        if (firebaseId == currentFirebaseId) return // Hindari re-inisialisasi yang tidak perlu
        currentFirebaseId = firebaseId

        viewModelScope.launch {
            _searchQuery
                .debounce(300) // Tunggu 300ms setelah user berhenti mengetik
                .distinctUntilChanged() // Hanya proses jika teks benar-benar berubah
                .flatMapLatest { query ->
                    // flatMapLatest akan membatalkan pemanggilan repository sebelumnya
                    // jika ada query baru yang masuk.
                    flow {
                        val result = courseRepository.getEnrolledCourses(
                            firebaseId = firebaseId,
                            query = query.ifBlank { null }
                        )
                        result.onSuccess { courses ->
                            emit(UserCoursesUiState.Success(courses))
                        }.onFailure { exception ->
                            emit(UserCoursesUiState.Error(exception.message ?: "Error"))
                        }
                    }.onStart { emit(UserCoursesUiState.Loading) } // Tampilkan loading setiap kali pencarian baru dimulai
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

    /**
     * Fungsi yang dipanggil oleh UI setiap kali teks di SearchBox berubah.
     */
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
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