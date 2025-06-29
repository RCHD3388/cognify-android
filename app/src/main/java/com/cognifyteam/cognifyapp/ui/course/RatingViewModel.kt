package com.cognifyteam.cognifyapp.ui.course

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cognifyteam.cognifyapp.data.models.Rating
import com.cognifyteam.cognifyapp.data.repositories.RatingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Sealed interface untuk merepresentasikan state UI saat memuat DAFTAR rating.
 */
sealed interface RatingsListUiState {
    data class Success(val ratings: List<Rating>) : RatingsListUiState
    data class Error(val message: String) : RatingsListUiState
    object Loading : RatingsListUiState
}

/**
 * Sealed interface untuk merepresentasikan state UI saat memuat SATU rating
 * milik pengguna yang sedang login.
 */
sealed interface MyRatingUiState {
    data class Success(val rating: Rating?) : MyRatingUiState // Rating bisa null jika belum ada
    data class Error(val message: String) : MyRatingUiState
    object Loading : MyRatingUiState
}

/**
 * ViewModel yang bertanggung jawab untuk semua logika yang berhubungan dengan rating kursus.
 */
class RatingViewModel(
    private val ratingRepository: RatingRepository
) : ViewModel() {

    // --- State untuk Daftar Semua Rating ---
    private val _ratingsListState = MutableStateFlow<RatingsListUiState>(RatingsListUiState.Loading)
    val ratingsListState: StateFlow<RatingsListUiState> = _ratingsListState.asStateFlow()

    // --- State untuk Rating Spesifik Milik User Login ---
    private val _myRatingState = MutableStateFlow<MyRatingUiState>(MyRatingUiState.Loading)
    val myRatingState: StateFlow<MyRatingUiState> = _myRatingState.asStateFlow()

    // --- State untuk Aksi Posting Rating ---
    // Digunakan sebagai event untuk menampilkan Toast/Snackbar atau menutup dialog.
    private val _postRatingState = MutableStateFlow<Result<Unit>?>(null)
    val postRatingState: StateFlow<Result<Unit>?> = _postRatingState.asStateFlow()

    /**
     * Memuat daftar semua rating untuk sebuah kursus.
     */
    fun loadRatings(courseId: String) {
        viewModelScope.launch {
            _ratingsListState.value = RatingsListUiState.Loading
            ratingRepository.getRatings(courseId)
                .onSuccess { ratings ->
                    _ratingsListState.value = RatingsListUiState.Success(ratings)
                }
                .onFailure { exception ->
                    _ratingsListState.value = RatingsListUiState.Error(exception.message ?: "Failed to load ratings")
                }
        }
    }

    /**
     * Memuat rating yang sudah pernah diberikan oleh user untuk kursus ini.
     */
    fun loadMyRating(courseId: String, firebaseId: String) {
        viewModelScope.launch {
            _myRatingState.value = MyRatingUiState.Loading
            ratingRepository.getMyRating(courseId, firebaseId)
                .onSuccess { rating ->
                    // `rating` di sini adalah `Rating?` (bisa null jika belum ada rating)
                    _myRatingState.value = MyRatingUiState.Success(rating)
                }
                .onFailure { exception ->
                    _myRatingState.value = MyRatingUiState.Error(exception.message ?: "Failed to load your rating")
                }
        }
    }

    /**
     * Mengirim rating baru atau memperbarui rating yang sudah ada.
     */
    fun submitRating(courseId: String, firebaseId: String, rating: Int, comment: String?) {
        viewModelScope.launch {
            // Beri sinyal ke UI bahwa proses posting dimulai (misal: untuk menampilkan loading di tombol)
            _postRatingState.value = Result.success(Unit).also { println("Submitting rating...") }

            val result = ratingRepository.postRating(courseId, firebaseId, rating, comment)

            // Kirim hasil akhir (sukses atau gagal) ke UI
            _postRatingState.value = result

            if (result.isSuccess) {
                // Jika posting berhasil, muat ulang daftar rating untuk menampilkan yang baru
                // dan juga muat ulang "My Rating" untuk memperbarui form.
                loadRatings(courseId)
                loadMyRating(courseId, firebaseId)
            }
        }
    }

    /**
     * Mereset state event posting setelah digunakan oleh UI (misal, setelah Toast ditampilkan).
     */
    fun onPostRatingConsumed() {
        _postRatingState.value = null
    }

    /**
     * Companion object yang menyediakan Factory untuk membuat instance ViewModel ini
     * dengan dependensi yang dibutuhkan (ratingRepository).
     */
    companion object {
        fun provideFactory(
            ratingRepository: RatingRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(RatingViewModel::class.java)) {
                    return RatingViewModel(ratingRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}