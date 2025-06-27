package com.cognifyteam.cognifyapp.ui.learningpath.viewmodel

import androidx.lifecycle.ViewModel
import com.cognifyteam.cognifyapp.ui.learningpath.screen.LearningPath
import com.cognifyteam.cognifyapp.ui.learningpath.screen.LearningPathStep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// --- DATA MODEL (Berdasarkan contoh dummy data Anda) ---

// --- UI STATE ---

data class LearningPathDetailUiState(
    val learningPath: LearningPath? = null,
    val isLoading: Boolean = true
)

// --- VIEWMODEL ---

class LearningPathDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LearningPathDetailUiState())
    val uiState: StateFlow<LearningPathDetailUiState> = _uiState.asStateFlow()

    // Daftar dummy data untuk semua learning path
    private val allLearningPaths: List<LearningPath> = listOf(
        LearningPath(
            id = 1,
            title = "Frontend Development Mastery",
            description = "Pelajari pengembangan frontend dari dasar hingga mahir. Mulai dari HTML, CSS, JavaScript, hingga framework modern seperti React dan Vue.js. Cocok untuk pemula yang ingin memulai karir sebagai frontend developer dengan pendekatan praktis dan project-based learning.",
            authorName = "Ahmad Sultoni",
            authorInitials = "AS",
            timeAgo = "2 hari yang lalu",
            level = "Pemula",
            tags = listOf("HTML", "CSS", "JavaScript", "React", "Vue.js", "Responsive Design"),
            likes = 234,
            liked_by_you = false,
            steps = listOf(
                LearningPathStep(1, "HTML & CSS Fundamentals", "Pelajari struktur dasar HTML dan styling dengan CSS. Termasuk semantic HTML, flexbox, grid, dan responsive design. Anda akan membuat 3 proyek: landing page, portfolio sederhana, dan layout responsive.", "2-3 minggu"),
                LearningPathStep(2, "JavaScript Basics", "Kuasai dasar-dasar JavaScript: variabel, fungsi, array, objek, dan DOM manipulation.", "3-4 minggu"),
                LearningPathStep(3, "React Fundamentals", "Belajar React: komponen, props, state, hooks, dan event handling.", "4-5 minggu"),
                LearningPathStep(4, "Advanced State Management", "Jelajahi Redux dan Context API untuk mengelola state aplikasi yang kompleks.", "2 minggu"),
                LearningPathStep(5, "Final Project: E-commerce App", "Bangun aplikasi e-commerce fungsional menggunakan React dan best practices.", "4 minggu")
            ),
            comments = 123
        ),
        LearningPath(
            id = 2,
            title = "Kotlin for Android Developers",
            description = "Menjadi Android Developer profesional dengan menguasai Kotlin. Dari sintaks dasar, coroutines, hingga integrasi dengan Jetpack Compose.",
            authorName = "Budi Sanjaya",
            authorInitials = "BS",
            timeAgo = "1 minggu yang lalu",
            level = "Menengah",
            tags = listOf("Kotlin", "Android", "Coroutines", "Jetpack Compose", "Mobile Dev"),
            likes = 512,
            liked_by_you = true,
            steps = listOf(
                LearningPathStep(1, "Kotlin Basics", "Memahami sintaks dasar, null safety, dan konsep dasar OOP di Kotlin.", "1-2 minggu"),
                LearningPathStep(2, "Kotlin Coroutines", "Menguasai asynchronous programming dengan coroutines untuk aplikasi yang responsif.", "2-3 minggu"),
                LearningPathStep(3, "Introduction to Jetpack Compose", "Membangun UI Android modern secara deklaratif dengan Jetpack Compose.", "3-4 minggu")
            ),
            comments = 456
        )
    )

    fun loadLearningPath(id: Int) {
        _uiState.update { it.copy(isLoading = true) }
        // Simulasi network delay
        // kotlinx.coroutines.delay(500)
        val path = allLearningPaths.find { it.id == id }
        _uiState.update { it.copy(learningPath = path, isLoading = false) }
    }

    fun toggleLike() {
        _uiState.update { currentState ->
            currentState.learningPath?.let { path ->
                val newLikedStatus = !path.liked_by_you
                val newLikesCount = if (newLikedStatus) path.likes + 1 else path.likes - 1

                // Membuat salinan learning path dengan data like yang baru
                val updatedPath = path.copy(
                    liked_by_you = newLikedStatus,
                    likes = newLikesCount
                )
                currentState.copy(learningPath = updatedPath)
            } ?: currentState
        }
    }
}