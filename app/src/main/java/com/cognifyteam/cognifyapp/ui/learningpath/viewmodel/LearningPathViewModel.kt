package com.cognifyteam.cognifyapp.ui.learningpath.screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cognifyteam.cognifyapp.data.models.LearningPathStep
import com.cognifyteam.cognifyapp.data.models.SmartLike
import com.cognifyteam.cognifyapp.data.repositories.smart.SmartRepository
import com.cognifyteam.cognifyapp.ui.learningpath.viewmodel.Comment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --- MODEL DATA (KEMBALI KE IMMUTABLE DENGAN `val`) ---
// Ini adalah praktik terbaik. Objek data seharusnya tidak bisa diubah dari luar.
data class LearningPath(
    val id: Int,
    val title: String,
    val description: String,
    val authorName: String,
    val authorInitials: String,
    val timeAgo: String,
    val level: String,
    val tags: List<String>,
    val likes: Int, // Kembali ke `val`
    val comments: Int,
    val comment_contents: List<Comment> = listOf(),
    val liked_by_you: Boolean,
    val steps: List<LearningPathStep>
)

private val filterCategories = listOf("Semua", "Programming", "Frontend", "Backend", "Design", "Data Science", "Marketing")

// --- UI STATE (Tidak ada perubahan) ---
data class LearningPathUiState(
    val searchQuery: String = "",
    val selectedCategory: String = "Semua",
    val learningPaths: List<com.cognifyteam.cognifyapp.data.models.LearningPath> = emptyList(),
    val allCategories: List<String> = emptyList()
)

// --- VIEWMODEL (DENGAN LOGIKA YANG DIPERBAIKI) ---
class LearningPathViewModel(
    private val smartRepository: SmartRepository
) : ViewModel() {

    // Sumber data utama sekarang menjadi state internal di dalam ViewModel
    // Ini memungkinkan kita untuk menggantinya dengan list baru saat ada perubahan
    private var _allLearningPaths: List<com.cognifyteam.cognifyapp.data.models.LearningPath> = listOf()
    private val _allCategories: List<String> = filterCategories

    private val _uiState = MutableStateFlow(LearningPathUiState())
    val uiState: StateFlow<LearningPathUiState> = _uiState.asStateFlow()

    fun loadInitialData() {
        viewModelScope.launch {
            val allResultLearningPaths = smartRepository.getAllLearningPaths()
            allResultLearningPaths.onSuccess {
                _allLearningPaths = it
                _uiState.value = LearningPathUiState(
                    learningPaths = _allLearningPaths,
                    allCategories = _allCategories,
                    selectedCategory = "Semua"
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        filterLearningPaths()
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        filterLearningPaths()
    }

    // =======================================================================
    // --- FUNGSI onLikeClicked DENGAN LOGIKA IMMUTABLE (SOLUSI) ---
    // =======================================================================
    fun onLikeClicked(pathId: Int, currentUserId: String) {
        viewModelScope.launch {
            smartRepository.likePath(smartId = pathId, userId = currentUserId)
            // Buat daftar baru dengan memetakan daftar lama
            val updatedPaths = _allLearningPaths.map { path ->
                if (path.id == pathId) {
                    if(path.likes.any { it.userId == currentUserId && it.smartId == path.id }){
                        path.copy(
                            likes = path.likes.filter { it.userId != currentUserId && it.smartId != path.id }
                        )
                    }else{
                        path.copy(
                            likes = path.likes + SmartLike(currentUserId, path.id)
                        )
                    }
                } else {
                    path
                }
            }
            Log.d("asdasd", "${updatedPaths}")

            _allLearningPaths = updatedPaths
            filterLearningPaths()
        }
    }

    private fun filterLearningPaths() {
        // Tidak perlu launch coroutine lagi di sini karena sudah dihandle di pemanggilnya
        val currentState = _uiState.value
        val query = currentState.searchQuery
        val category = currentState.selectedCategory

        val filteredList = _allLearningPaths.filter { path ->
            val matchesSearchQuery = if (query.isBlank()) {
                true
            } else {
                path.title.contains(query, ignoreCase = true)
            }

            val matchesCategory = if (category == "Semua") {
                true
            } else {
                path.tags.any { tag -> tag.equals(category, ignoreCase = true) }
            }

            matchesSearchQuery && matchesCategory
        }

        // Karena kita selalu membuat objek baru, Compose pasti akan mendeteksi perubahan.
        _uiState.update { it.copy(learningPaths = filteredList) }
    }

    companion object {
        fun provideFactory(
            smartRepository: SmartRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LearningPathViewModel(smartRepository) as T
            }
        }
    }
}