package com.cognifyteam.cognifyapp.ui.learningpath.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cognifyteam.cognifyapp.ui.learningpath.screen.LearningPath
import com.cognifyteam.cognifyapp.ui.learningpath.screen.LearningPathStep
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
    val learningPath: LearningPath? = null,
    val isLoading: Boolean = true,
    val commentInput: String = "" // State untuk field input komentar
)

// --- VIEWMODEL (Diperbarui dengan logika komentar) ---

class LearningPathDetailViewModel : ViewModel() {

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
                LearningPathStep(1, "HTML & CSS Fundamentals", "...", "2-3 minggu"),
                LearningPathStep(2, "JavaScript Basics", "...", "3-4 minggu"),
                LearningPathStep(3, "React Fundamentals", "...", "4-5 minggu"),
                LearningPathStep(4, "Advanced State Management", "...", "2 minggu"),
                LearningPathStep(5, "Final Project: E-commerce App", "...", "4 minggu")
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
            delay(500) // Simulasi network delay
            val path = allLearningPaths.find { it.id == id }
            _uiState.update { it.copy(learningPath = path, isLoading = false) }
        }
    }

    fun toggleLike() {
        _uiState.update { currentState ->
            currentState.learningPath?.let { path ->
                val newLikedStatus = !path.liked_by_you
                val newLikesCount = if (newLikedStatus) path.likes + 1 else path.likes - 1
                val updatedPath = path.copy(liked_by_you = newLikedStatus, likes = newLikesCount)
                currentState.copy(learningPath = updatedPath)
            } ?: currentState
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
    fun postComment() {
        val commentText = _uiState.value.commentInput.trim()
        if (commentText.isBlank()) return // Jangan post jika kosong

        _uiState.update { currentState ->
            currentState.learningPath?.let { path ->
                val newComment = Comment(
                    authorName = "Anda", // Nama pengguna yang sedang login
                    authorInitials = "A",
                    timestamp = "Baru saja",
                    content = commentText
                )
                // Menambahkan komentar baru ke awal daftar
                val updatedComments = listOf(newComment) + path.comment_contents
                val updatedPath = path.copy(comment_contents = updatedComments)

                // Mengembalikan state baru dengan komentar terkirim dan input field dikosongkan
                currentState.copy(learningPath = updatedPath, commentInput = "")

            } ?: currentState
        }
    }
}