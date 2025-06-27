package com.cognifyteam.cognifyapp.ui.learningpath.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val liked_by_you: Boolean,
    val steps: List<LearningPathStep>
)

// --- DUMMY DATA (Tidak perlu mutable lagi) ---
private val sampleLearningPaths = listOf(
    // ... data dummy tetap sama, tapi sekarang cocok dengan data class yang immutable
    LearningPath(id = 1, title = "Frontend Development Mastery", description = "...", authorName = "Ahmad Sultoni", authorInitials = "AS", timeAgo = "2 jam yang lalu", level = "Pemula", tags = listOf("HTML", "CSS", "JavaScript", "React", "Programming"), likes = 234, comments = 45, liked_by_you = false, steps = emptyList()),
    LearningPath(id = 2, title = "UI/UX Design Fundamentals", description = "...", authorName = "Maria Rosanti", authorInitials = "MR", timeAgo = "4 jam yang lalu", level = "Menengah", tags = listOf("UI Design", "UX Research", "Figma", "Prototyping", "Design"), likes = 189, comments = 32, liked_by_you = true, steps = emptyList()),
    LearningPath(id = 3, title = "Data Science with Python", description = "...", authorName = "Budi Santoso", authorInitials = "BS", timeAgo = "1 hari yang lalu", level = "Menengah", tags = listOf("Python", "Pandas", "Data Science", "Machine Learning"), likes = 305, comments = 68, liked_by_you = false, steps = emptyList()),
    LearningPath(id = 4, title = "Digital Marketing 101", description = "...", authorName = "Cyntia Bella", authorInitials = "CB", timeAgo = "3 hari yang lalu", level = "Pemula", tags = listOf("SEO", "Social Media", "Marketing"), likes = 152, comments = 21, liked_by_you = true, steps = emptyList())
)

private val filterCategories = listOf("Semua", "Programming", "Design", "Data Science", "Marketing")

// --- UI STATE (Tidak ada perubahan) ---
data class LearningPathUiState(
    val searchQuery: String = "",
    val selectedCategory: String = "Semua",
    val learningPaths: List<LearningPath> = emptyList(),
    val allCategories: List<String> = emptyList()
)

// --- VIEWMODEL (DENGAN LOGIKA YANG DIPERBAIKI) ---
class LearningPathViewModel : ViewModel() {

    // Sumber data utama sekarang menjadi state internal di dalam ViewModel
    // Ini memungkinkan kita untuk menggantinya dengan list baru saat ada perubahan
    private var _allLearningPaths: List<LearningPath> = sampleLearningPaths
    private val _allCategories: List<String> = filterCategories

    private val _uiState = MutableStateFlow(LearningPathUiState())
    val uiState: StateFlow<LearningPathUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        _uiState.value = LearningPathUiState(
            learningPaths = _allLearningPaths,
            allCategories = _allCategories,
            selectedCategory = "Semua"
        )
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
    fun onLikeClicked(pathId: Int) {
        viewModelScope.launch {
            // Buat daftar baru dengan memetakan daftar lama
            val updatedPaths = _allLearningPaths.map { path ->
                // Jika path ini adalah yang kita cari...
                if (path.id == pathId) {
                    // ...buat objek BARU dengan nilai yang diperbarui.
                    path.copy(
                        liked_by_you = !path.liked_by_you,
                        likes = if (path.liked_by_you) path.likes - 1 else path.likes + 1
                    )
                } else {
                    // ...jika tidak, kembalikan objek path seperti semula.
                    path
                }
            }
            // Ganti sumber data utama kita dengan daftar yang baru ini
            _allLearningPaths = updatedPaths

            // Panggil filter untuk menerapkan logika pencarian/kategori ke data baru
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
}