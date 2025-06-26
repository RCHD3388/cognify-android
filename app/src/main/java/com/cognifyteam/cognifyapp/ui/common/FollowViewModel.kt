package com.cognifyteam.cognifyapp.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.data.repositories.FollowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Sealed interface untuk merepresentasikan state UI
 * saat memuat daftar user (baik following maupun followers).
 */
sealed interface FollowListUiState {
    data class Success(val users: List<User>) : FollowListUiState
    data class Error(val message: String) : FollowListUiState
    object Loading : FollowListUiState
}

/**
 * ViewModel yang bertanggung jawab untuk semua logika yang berhubungan dengan follow/unfollow,
 * serta mengambil daftar following dan followers.
 */
class FollowViewModel(
    private val followRepository: FollowRepository
) : ViewModel() {

    // StateFlow untuk daftar 'following'
    private val _followingState = MutableStateFlow<FollowListUiState>(FollowListUiState.Loading)
    val followingState: StateFlow<FollowListUiState> = _followingState.asStateFlow()

    // StateFlow untuk daftar 'followers'
    private val _followersState = MutableStateFlow<FollowListUiState>(FollowListUiState.Loading)
    val followersState: StateFlow<FollowListUiState> = _followersState.asStateFlow()

    // State terpisah untuk aksi follow/unfollow agar tidak mengganggu list utama
    private val _followActionState = MutableStateFlow<Result<Unit>?>(null)
    val followActionState: StateFlow<Result<Unit>?> = _followActionState.asStateFlow()

    /**
     * Memuat daftar user yang DI-FOLLOW oleh user dengan ID tertentu.
     */
    fun loadFollowing(userId: String) {
        viewModelScope.launch {
            _followingState.value = FollowListUiState.Loading
            followRepository.getFollowing(userId)
                .onSuccess { users -> _followingState.value = FollowListUiState.Success(users) }
                .onFailure { e -> _followingState.value = FollowListUiState.Error(e.message ?: "Error") }
        }
    }

    /**
     * Memuat daftar user yang MENGIKUTI user dengan ID tertentu.
     */
    fun loadFollowers(userId: String) {
        viewModelScope.launch {
            _followersState.value = FollowListUiState.Loading
            followRepository.getFollowers(userId)
                .onSuccess { users -> _followersState.value = FollowListUiState.Success(users) }
                .onFailure { e -> _followersState.value = FollowListUiState.Error(e.message ?: "Error") }
        }
    }

    /**
     * Melakukan aksi follow terhadap seorang user.
     * @param followerId ID dari user yang sedang login (yang melakukan aksi).
     * @param userIdToFollow ID dari user yang akan di-follow.
     */
    fun followUser(followerId: String, userIdToFollow: String) {
        viewModelScope.launch {
            val result = followRepository.followUser(followerId, userIdToFollow)
            _followActionState.value = result
            // Bisa tambahkan logika untuk refresh daftar setelah follow
            // loadFollowing(followerId)
        }
    }

    /**
     * Melakukan aksi unfollow terhadap seorang user.
     * @param followerId ID dari user yang sedang login (yang melakukan aksi).
     *_@param userIdToUnfollow ID dari user yang akan di-unfollow.
     */
    fun unfollowUser(followerId: String, userIdToUnfollow: String) {
        viewModelScope.launch {
            val result = followRepository.unfollowUser(followerId, userIdToUnfollow)
            _followActionState.value = result
            // Bisa tambahkan logika untuk refresh daftar setelah unfollow
            // loadFollowing(followerId)
        }
    }

    /**
     * Mereset state aksi follow/unfollow setelah ditampilkan di UI (misal, setelah Toast).
     */
    fun onFollowActionConsumed() {
        _followActionState.value = null
    }

    companion object {
        fun provideFactory(
            followRepository: FollowRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(FollowViewModel::class.java)) {
                    return FollowViewModel(followRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}