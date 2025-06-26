package com.cognifyteam.cognifyapp.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cognifyteam.cognifyapp.data.repositories.FollowRepository
import com.cognifyteam.cognifyapp.data.sources.remote.UserUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SearchUiState {
    data class Success(val users: List<UserUiState>) : SearchUiState // <-- Menggunakan UserUiState
    data class Error(val message: String) : SearchUiState
    object Loading : SearchUiState
    object EmptyQuery : SearchUiState
    object EmptyResult : SearchUiState
}

class SearchViewModel(
    private val followRepository: FollowRepository // <-- Tambahkan FollowRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.EmptyQuery)
    val uiState: StateFlow<SearchUiState> = _uiState

    // State untuk menampung daftar ID orang yang di-follow oleh user yang login
    private val _loggedInUserFollowingIds = MutableStateFlow<Set<String>>(emptySet())

    private var searchJob: Job? = null

    /**
     * Fungsi ini harus dipanggil sekali saat ViewModel dibuat untuk memuat
     * daftar following dari user yang login.
     */
    fun loadInitialFollowingState(loggedInUserId: String) {
        viewModelScope.launch {
            followRepository.getFollowing(loggedInUserId)
                .onSuccess { followingList ->
                    // Simpan hanya ID-nya ke dalam Set untuk pengecekan yang cepat
                    _loggedInUserFollowingIds.value = followingList.map { it.firebaseId }.toSet()
                }
        }
    }

    /**
     * Melakukan pencarian dengan debounce dan memperkaya hasil dengan status follow.
     */
    fun onSearchQueryChanged(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _uiState.value = SearchUiState.EmptyQuery
            return
        }

        searchJob = viewModelScope.launch {
            delay(500) // Debounce
            _uiState.value = SearchUiState.Loading
            followRepository.searchUsers(query)
                .onSuccess { usersFromRepo ->
                    // Mapping dari List<User> ke List<UserUiState>
                    val uiUsers = usersFromRepo.map { user ->
                        UserUiState(
                            user = user,
                            // Cek apakah ID user ini ada di dalam daftar following kita
                            isFollowing = _loggedInUserFollowingIds.value.contains(user.firebaseId)
                        )
                    }
                    _uiState.value = if (uiUsers.isEmpty()) {
                        SearchUiState.EmptyResult
                    } else {
                        SearchUiState.Success(uiUsers)
                    }
                }
                .onFailure { e ->
                    _uiState.value = SearchUiState.Error(e.message ?: "Search failed")
                }
        }
    }

    /**
     * Fungsi untuk memperbarui UI secara optimis saat tombol follow/unfollow diklik.
     */
    fun toggleFollowState(targetUserId: String) {
        val currentState = _uiState.value
        if (currentState is SearchUiState.Success) {
            val updatedUsers = currentState.users.map { uiUser ->
                if (uiUser.user.firebaseId == targetUserId) {
                    uiUser.copy(isFollowing = !uiUser.isFollowing)
                } else {
                    uiUser
                }
            }
            _uiState.value = SearchUiState.Success(updatedUsers)

            // Perbarui juga daftar ID following lokal
            val currentFollowing = _loggedInUserFollowingIds.value.toMutableSet()
            if (currentFollowing.contains(targetUserId)) {
                currentFollowing.remove(targetUserId)
            } else {
                currentFollowing.add(targetUserId)
            }
            _loggedInUserFollowingIds.value = currentFollowing
        }
    }

    companion object {
        fun provideFactory(
            followRepository: FollowRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
                    return SearchViewModel(followRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}