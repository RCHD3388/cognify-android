package com.cognifyteam.cognifyapp.ui.learningpath.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cognifyteam.cognifyapp.data.models.LearningPathStep
import com.cognifyteam.cognifyapp.data.models.SmartComment
import com.cognifyteam.cognifyapp.data.models.SmartLike
import com.cognifyteam.cognifyapp.data.repositories.smart.SmartRepository
import com.cognifyteam.cognifyapp.ui.learningpath.screen.AddNewLearningPathViewModel
import com.cognifyteam.cognifyapp.ui.learningpath.screen.LearningPath
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

// --- DATA MODEL (Termasuk model baru untuk Komentar) ---

data class Comment(
    val id: String = UUID.randomUUID().toString(), // ID unik untuk key di LazyColumn
    val authorName: String,
    val authorInitials: String,
    val timestamp: String,
    val content: String,
)

// --- UI STATE (Diperbarui dengan state untuk komentar) ---

data class LearningPathDetailUiState(
    val learningPath: com.cognifyteam.cognifyapp.data.models.LearningPath? = null,
    val isLoading: Boolean = true,
    val commentInput: String = "" // State untuk field input komentar
)

// --- VIEWMODEL (Diperbarui dengan logika komentar) ---

class LearningPathDetailViewModel(
    private val smartRepository: SmartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LearningPathDetailUiState())
    val uiState: StateFlow<LearningPathDetailUiState> = _uiState.asStateFlow()

    // Daftar dummy data diperbarui dengan data komentar
    private val allLearningPaths: List<LearningPath> = listOf(
        LearningPath(
            id = 1,
            title = "Frontend Development Mastery",
            description = "Pelajari pengembangan frontend dari dasar hingga mahir...",
            authorName = "Ahmad Sultoni", authorInitials = "AS", timeAgo = "2 hari yang lalu",
            level = "Pemula", tags = listOf("HTML", "CSS", "JavaScript", "React", "Vue.js"),
            likes = 234, liked_by_you = false,
            steps = listOf(
                LearningPathStep(1, "HTML & CSS Fundamentals", "...", "2-3 minggu", 1, 1),
                LearningPathStep(2, "JavaScript Basics", "...", "3-4 minggu", 1, 2),
                LearningPathStep(3, "React Fundamentals", "...", "4-5 minggu", 1, 3),
                LearningPathStep(4, "Advanced State Management", "...", "2 minggu", 1, 4),
                LearningPathStep(5, "Final Project: E-commerce App", "...", "4 minggu", 1, 5)
            ),
            comments = 123,
            comment_contents = listOf(
                Comment(authorName = "Maria Rosanti", authorInitials = "MR", timestamp = "3 jam yang lalu", content = "Learning path ini sangat terstruktur! Saya sudah selesai sampai step 2 dan merasa sangat terbantu. Penjelasan JavaScript-nya detail banget dan proyeknya real-world. Recommended untuk pemula! 👍"),
                Comment(authorName = "Dika Kurniawan", authorInitials = "DK", timestamp = "5 jam yang lalu", content = "Apakah learning path ini cocok untuk yang sudah punya background programming tapi baru di frontend? Saya dari backend Java, pengen switch ke frontend."),
                Comment(authorName = "Sari Puspita", authorInitials = "SP", timestamp = "1 hari yang lalu", content = "Terima kasih Ahmad! Final project e-commerce-nya challenging tapi sangat worth it. Sekarang saya sudah diterima sebagai frontend developer junior. Learning path ini benar-benar game changer! 🚀"),
                Comment(authorName = "Rudi Hermawan", authorInitials = "RH", timestamp = "2 hari yang lalu", content = "Step-by-step nya jelas dan timeline-nya realistic. Saya suka ada proyek di setiap step, jadi bisa langsung practice. Untuk step 3 React, ada rencana buat video tutorial juga?"),
            )
        ),
        // ... Learning path lain bisa ditambahkan di sini
    )

    fun loadLearningPath(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val fetchedLearningPath = smartRepository.getOneLearningPath(id)
            val path = fetchedLearningPath
            path.onSuccess { newpath ->
                _uiState.update { it.copy(learningPath = newpath, isLoading = false) }
            }
        }
    }

    fun toggleLike(currentUserId: String) {
        viewModelScope.launch {
            if(_uiState.value.learningPath != null) {
                val repoResult = smartRepository.likePath(
                    smartId = _uiState.value.learningPath!!.id,
                    userId = currentUserId
                )
                repoResult.onSuccess {
                    _uiState.update { currentState ->
                        currentState.learningPath?.let { path ->
                            var updatedPath: com.cognifyteam.cognifyapp.data.models.LearningPath? =
                                null;
                            if (path.likes.any { it.userId == currentUserId && it.smartId == path.id }) {
                                updatedPath = path.copy(
                                    likes = path.likes.filter { it.userId != currentUserId && it.smartId != path.id }
                                )
                            } else {
                                updatedPath = path.copy(
                                    likes = path.likes + SmartLike(
                                        currentUserId,
                                        path.id,
                                        it.toInt()
                                    )
                                )
                            }
                            currentState.copy(learningPath = updatedPath)
                        } ?: currentState
                    }
                }
            }
        }
    }

    // --- FUNGSI BARU UNTUK KOMENTAR ---

    /**
     * Dipanggil setiap kali teks di field komentar berubah.
     */
    fun onCommentInputChange(newText: String) {
        // Batasi panjang teks hingga 500 karakter
        if (newText.length <= 500) {
            _uiState.update { it.copy(commentInput = newText) }
        }
    }

    /**
     * Dipanggil saat tombol "Kirim" ditekan.
     */
    fun postComment(currentUserId: String) {
        val commentText = _uiState.value.commentInput.trim()
        if (commentText.isBlank()) return // Jangan post jika kosong

        viewModelScope.launch {
            if(_uiState.value.learningPath != null){
                val resResult = smartRepository.addNewPost(currentUserId, _uiState.value.learningPath!!.id, commentText)
                resResult.onSuccess {
                    _uiState.update { currentState ->
                        currentState.learningPath?.let { path ->
                            val newComment = it
                            // Menambahkan komentar baru ke awal daftar
                            val updatedComments = listOf(newComment) + path.comments
                            val updatedPath = path.copy(comments = updatedComments)

                            // Mengembalikan state baru dengan komentar terkirim dan input field dikosongkan
                            currentState.copy(learningPath = updatedPath, commentInput = "")
                        } ?: currentState
                    }
                }
            }
        }
    }

    companion object {
        fun provideFactory(
            smartRepository: SmartRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LearningPathDetailViewModel(smartRepository) as T
            }
        }
    }
}