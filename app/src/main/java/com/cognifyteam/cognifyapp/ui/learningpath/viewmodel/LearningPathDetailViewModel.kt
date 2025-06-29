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
    val commentInput: String = "", // State untuk field input komentar
    val resultState: String = ""
)

// --- VIEWMODEL (Diperbarui dengan logika komentar) ---

class LearningPathDetailViewModel(
    private val smartRepository: SmartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LearningPathDetailUiState())
    val uiState: StateFlow<LearningPathDetailUiState> = _uiState.asStateFlow()

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

    fun deletePath(currentUserId: String){
        viewModelScope.launch {
            val result = smartRepository.deletePath(_uiState.value.learningPath!!.id)
            result.onSuccess {
                _uiState.update { currentState ->
                    currentState.resultState.let { state ->
                        currentState.copy(resultState = "Deleted")
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