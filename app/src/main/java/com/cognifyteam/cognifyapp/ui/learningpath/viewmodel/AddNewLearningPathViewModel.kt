package com.cognifyteam.cognifyapp.ui.learningpath.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Enum untuk merepresentasikan state layar saat ini
enum class LearningPathScreenState {
    FORM,    // Tampilan form input
    LOADING, // Tampilan saat proses generate
    RESULT   // Tampilan hasil learning path
}

// Data class untuk satu langkah dalam learning path
data class LearningPathStep(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val estimatedTime: String
)

// Data class untuk menampung semua state dari layar
data class AddLearningPathUiState(
    // State untuk Form
    val predefinedTopics: List<String> = listOf(
        "Frontend Development", "Backend Development", "Mobile Development",
        "Data Science", "AI & Machine Learning", "DevOps",
        "UI/UX Design", "Cyber Security", "Blockchain", "Cloud Computing"
    ),
    val selectedTopic: String? = null,
    val customTopic: String = "",
    val additionalPrompt: String = "",
    val learningLevels: List<String> = listOf("Pemula", "Menengah", "Lanjutan"),
    val selectedLevel: String = "Pemula",
    val isGenerateButtonEnabled: Boolean = false,

    // State untuk mengontrol tampilan
    val screenState: LearningPathScreenState = LearningPathScreenState.FORM,

    // State untuk Hasil
    val generatedPath: List<LearningPathStep> = emptyList()
)

class AddNewLearningPathViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AddLearningPathUiState())
    val uiState: StateFlow<AddLearningPathUiState> = _uiState.asStateFlow()

    // --- Fungsi untuk bagian Form ---
    fun onTopicSelected(topic: String) {
        _uiState.update { currentState ->
            val newSelectedTopic = if (currentState.selectedTopic == topic) null else topic
            currentState.copy(
                selectedTopic = newSelectedTopic,
                customTopic = ""
            ).recalculateButtonState()
        }
    }

    fun onCustomTopicChanged(text: String) {
        _uiState.update { currentState ->
            currentState.copy(
                customTopic = text,
                selectedTopic = null
            ).recalculateButtonState()
        }
    }

    fun onAdditionalPromptChanged(text: String) {
        if (text.length <= 500) {
            _uiState.update { it.copy(additionalPrompt = text) }
        }
    }

    fun onLevelSelected(level: String) {
        _uiState.update { currentState ->
            currentState.copy(selectedLevel = level).recalculateButtonState()
        }
    }

    // --- Fungsi untuk bagian Aksi (Generate, Save, etc.) ---

    fun onGenerateClicked() {
        viewModelScope.launch {
            // 1. Ubah state ke LOADING
            _uiState.update { it.copy(screenState = LearningPathScreenState.LOADING) }

            // 2. Simulasi proses generate (misalnya call API)
            delay(2000) // delay 2 detik

            // 3. Buat data dummy dan update state ke RESULT
            _uiState.update {
                it.copy(
                    screenState = LearningPathScreenState.RESULT,
                    generatedPath = createDummyPathData()
                )
            }
        }
    }

    fun onRegenerateClicked() {
        // Kembali ke tampilan form
        _uiState.update {
            it.copy(
                screenState = LearningPathScreenState.FORM
                // State form lainnya (seperti topic, level) tetap tersimpan
            )
        }
    }

    fun onSaveClicked() {
        // Logika untuk menyimpan learning path ke database/API
        println("Learning Path Saved!")
        // Di sini Anda bisa menavigasi ke layar lain atau menampilkan Snackbar
    }


    // --- Fungsi Helper ---

    private fun AddLearningPathUiState.recalculateButtonState(): AddLearningPathUiState {
        val isTopicFilled = selectedTopic != null || customTopic.isNotBlank()
        val isLevelSelected = selectedLevel.isNotBlank()
        return this.copy(isGenerateButtonEnabled = isTopicFilled && isLevelSelected)
    }

    private fun createDummyPathData(): List<LearningPathStep> {
        // Data dummy bisa lebih dari 5
        return listOf(
            LearningPathStep(1, "HTML & CSS Fundamentals", "Pelajari struktur dasar HTML dan styling dengan CSS. Termasuk flexbox, grid, dan responsive design.", "2-3 minggu"),
            LearningPathStep(2, "JavaScript Basics", "Kuasai dasar-dasar JavaScript: variabel, fungsi, array, objek, dan DOM manipulation.", "3-4 minggu"),
            LearningPathStep(3, "React Fundamentals", "Belajar React: komponen, props, state, hooks, dan event handling.", "4-5 minggu"),
//            LearningPathStep(4, "State Management", "Pahami Redux atau Context API untuk mengelola state aplikasi React.", "2-3 minggu"),
//            LearningPathStep(5, "API Integration", "Belajar cara mengambil dan mengirim data ke server menggunakan Fetch API atau Axios.", "1-2 minggu"),
//            LearningPathStep(6, "Styling in React", "Eksplorasi Styled Components atau Tailwind CSS untuk styling yang lebih modular dan efisien.", "2-3 minggu"),
//            LearningPathStep(7, "Final Project", "Buat aplikasi e-commerce lengkap dengan React, termasuk API integration.", "3-4 minggu")
        )
    }
}