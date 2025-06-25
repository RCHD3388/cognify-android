package com.cognifyteam.cognifyapp.ui.course

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cognifyteam.cognifyapp.data.models.Discussion
import com.cognifyteam.cognifyapp.data.repositories.DiscussionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Sealed interface untuk merepresentasikan semua kemungkinan state UI
 * saat memuat daftar diskusi kursus.
 */
sealed interface DiscussionUiState {
    /**
     * State ketika data berhasil dimuat.
     * @param discussions Daftar diskusi (termasuk balasannya) yang akan ditampilkan.
     */
    data class Success(val discussions: List<Discussion>) : DiscussionUiState

    /**
     * State ketika terjadi error saat mengambil data.
     * @param message Pesan error yang bisa ditampilkan ke pengguna.
     */
    data class Error(val message: String) : DiscussionUiState

    /**
     * State ketika data sedang dimuat dari repository.
     */
    object Loading : DiscussionUiState
}

/**
 * ViewModel yang bertanggung jawab untuk mengambil dan menampung
 * state dari daftar diskusi untuk sebuah kursus.
 */
class DiscussionViewModel(
    private val discussionRepository: DiscussionRepository
) : ViewModel() {

    // StateFlow privat untuk menampung state, hanya bisa diubah di dalam ViewModel.
    // Diinisialisasi dengan state Loading agar UI langsung menampilkan spinner.
    private val _uiState = MutableStateFlow<DiscussionUiState>(DiscussionUiState.Loading)

    // StateFlow publik yang hanya bisa dibaca (read-only) oleh UI.
    val uiState: StateFlow<DiscussionUiState> = _uiState

    /**
     * Memulai proses untuk mengambil daftar diskusi dari repository.
     * @param courseId ID dari kursus yang diskusinya akan diambil.
     */
    fun loadDiscussions(courseId: String) {
        viewModelScope.launch {
            // Set state menjadi Loading setiap kali fungsi ini dipanggil (misal: untuk refresh)
            _uiState.value = DiscussionUiState.Loading

            // Panggil repository untuk mendapatkan hasilnya
            val result = discussionRepository.getDiscussionsForCourse(courseId)

            // "Buka" hasil dari repository menggunakan onSuccess dan onFailure
            result.onSuccess { discussions ->
                // Jika sukses, perbarui state dengan daftar diskusi
                _uiState.value = DiscussionUiState.Success(discussions)
            }.onFailure { exception ->
                // Jika gagal, perbarui state dengan pesan error
                _uiState.value = DiscussionUiState.Error(exception.message ?: "Failed to load discussions")
            }
        }
    }

    fun addDiscussion(courseId: String, firebaseId: String, content: String) {
        viewModelScope.launch {
            // Kita tidak mengubah _uiState menjadi Loading agar UI tidak berkedip
            val result = discussionRepository.createPost(firebaseId, courseId, content)

            result.onSuccess { newPost ->
                // Jika berhasil, perbarui state UI secara optimis
                val currentState = _uiState.value
                if (currentState is DiscussionUiState.Success) {
                    // Tambahkan post baru ke awal daftar
                    _uiState.value = currentState.copy(
                        discussions = listOf(newPost) + currentState.discussions
                    )
                }
            }.onFailure { exception ->
            }
        }
    }

    fun addReply(parentId: Int, firebaseId: String, content: String) {
        viewModelScope.launch {
            val result = discussionRepository.createReply(parentId, firebaseId, content)

            result.onSuccess { newReply ->
                // Perbarui state UI dengan balasan yang baru
                val currentState = _uiState.value
                if (currentState is DiscussionUiState.Success) {
                    val updatedDiscussions = currentState.discussions.map { discussion ->
                        if (discussion.id == parentId) {
                            // Tambahkan balasan baru ke post yang benar
                            discussion.copy(replies = discussion.replies + newReply)
                        } else {
                            discussion
                        }
                    }
                    _uiState.value = currentState.copy(discussions = updatedDiscussions)
                }
            }.onFailure { exception ->
            }
        }
    }

    /**
     * Companion object yang menyediakan Factory untuk membuat instance ViewModel ini
     * dengan dependensi yang dibutuhkan (discussionRepository).
     */
    companion object {
        fun provideFactory(
            discussionRepository: DiscussionRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(DiscussionViewModel::class.java)) {
                    return DiscussionViewModel(discussionRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}