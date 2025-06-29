package com.cognifyteam.cognifyapp.ui.learningpath.screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cognifyteam.cognifyapp.data.models.GeneratedLearningPath
import com.cognifyteam.cognifyapp.data.models.LearningPathStep
import com.cognifyteam.cognifyapp.data.repositories.auth.AuthRepository
import com.cognifyteam.cognifyapp.data.repositories.smart.SmartRepository
import com.cognifyteam.cognifyapp.ui.auth.AuthUiState
import com.cognifyteam.cognifyapp.ui.auth.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Enum untuk merepresentasikan state layar saat ini
enum class LearningPathScreenState {
    FORM,    // Tampilan form input
    LOADING, // Tampilan saat proses generate
    RESULT,   // Tampilan hasil learning path
    FAILED_GENERATE,
    SAVE_RESULT
}

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
    val screenMessage: String = "",

    // State untuk Hasil

    val generatedPath: GeneratedLearningPath? = null,

    val learningPathTitle: String = "",
    val isSaveButtonEnabled: Boolean = false // Tambahkan state ini
)

class AddNewLearningPathViewModel(
    private val smartRepository: SmartRepository
) : ViewModel() {

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

    fun resetState() {
        _uiState.update { curState ->
            curState.copy(screenState = LearningPathScreenState.FORM)
        }
    }

    fun onLearningPathTitleChanged(title: String) {
        _uiState.update {
            it.copy(
                learningPathTitle = title,
                isSaveButtonEnabled = title.isNotBlank() // Aktifkan tombol jika judul tidak kosong
            )
        }
    }

    // --- Fungsi untuk bagian Aksi (Generate, Save, etc.) ---

    fun onGenerateClicked() {
        viewModelScope.launch {
            val mainTopic = uiState.value.selectedTopic ?: uiState.value.customTopic
            val additionalPrompt = uiState.value.additionalPrompt ?: "Sesuaikan saja dengan topic dan level yang diminta"
            val level = uiState.value.selectedLevel

            // 1. Ubah state ke LOADING
            _uiState.update { it.copy(screenState = LearningPathScreenState.LOADING) }

            // 2. generate (call API)
            val backendResult = smartRepository.generateNewLP(topic = mainTopic, level, additional_prompt = additionalPrompt)

            backendResult.onSuccess { newLearningPath ->
                _uiState.update {
                    it.copy(
                        screenState = LearningPathScreenState.RESULT,
                        generatedPath = newLearningPath
                    )
                }
            }.onFailure {
                val originalErrorMessage = backendResult.exceptionOrNull()?.message
                Log.e("AddNewLearningPathViewModel", "Fail to Generate: $originalErrorMessage")
                _uiState.update {
                    it.copy(
                        screenState = LearningPathScreenState.FAILED_GENERATE,
                        screenMessage = "Gagal membuat learning path. Silakan coba lagi nanti."
                    )
                }
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

    fun onSaveClicked(currentUser: String) {
        viewModelScope.launch {
            val learningPath = uiState.value.generatedPath
            val title = uiState.value.learningPathTitle
            _uiState.update { it.copy(screenState = LearningPathScreenState.LOADING) }

            val backendResult = smartRepository.saveNewLP(userId = currentUser, title, learningPath!!)
            Log.d("AddNewLearningPathViewModel", "Save Result: $backendResult")

            backendResult.fold(
                onSuccess = { savedData ->
                    // Ini akan dieksekusi jika `backendResult` adalah Result.success
                    Log.i("AddNewLearningPathViewModel", "Save successful: $savedData")
                    _uiState.update {
                        it.copy(
                            screenState = LearningPathScreenState.SAVE_RESULT,
                            screenMessage = "Learning Path saved successfully!"
                        )
                    }
                },
                onFailure = { exception ->
                    // Ini akan dieksekusi jika `backendResult` adalah Result.failure
                    Log.e("AddNewLearningPathViewModel", "Save failed", exception)
                    _uiState.update {
                        it.copy(
                            // Sebaiknya ada state ERROR untuk menampilkan pesan error dengan benar
                            screenState = LearningPathScreenState.SAVE_RESULT,
                            // Ambil pesan dari exception, atau berikan pesan default jika tidak ada
                            screenMessage = exception.message ?: "Save failed. Please check your internet connection."
                        )
                    }
                }
            )
        }
    }


    // --- Fungsi Helper ---

    private fun AddLearningPathUiState.recalculateButtonState(): AddLearningPathUiState {
        val isTopicFilled = selectedTopic != null || customTopic.isNotBlank()
        val isLevelSelected = selectedLevel.isNotBlank()
        return this.copy(isGenerateButtonEnabled = isTopicFilled && isLevelSelected)
    }

    companion object {
        fun provideFactory(
            smartRepository: SmartRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AddNewLearningPathViewModel(smartRepository) as T
            }
        }
    }
}