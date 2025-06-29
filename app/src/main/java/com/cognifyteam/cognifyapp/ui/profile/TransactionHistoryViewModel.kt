package com.cognifyteam.cognifyapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cognifyteam.cognifyapp.data.models.Transaction
import com.cognifyteam.cognifyapp.data.repositories.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

// State untuk UI tidak berubah
sealed interface TransactionUiState {
    object Loading : TransactionUiState
    data class Success(val transactions: List<Transaction>) : TransactionUiState
    data class Error(val message: String) : TransactionUiState
}

class TransactionHistoryViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TransactionUiState>(TransactionUiState.Loading)
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    fun loadUserTransactions(userId: String) {
        viewModelScope.launch {
            // Kita tidak set Loading di sini lagi, karena Flow bisa emit beberapa kali.
            // UI akan menampilkan Loading sampai emisi pertama.

            transactionRepository.getUserTransactions(userId)
                .catch { exception ->
                    // Tangani error yang mungkin terjadi di dalam Flow itu sendiri
                    _uiState.value = TransactionUiState.Error(exception.message ?: "An unexpected error occurred")
                }
                .collect { result -> // Gunakan .collect() pada Flow
                    // Setiap kali Repository melakukan 'emit', blok ini akan berjalan.
                    result.onSuccess { transactions ->
                        // Update state dengan data terbaru (dari cache atau network)
                        _uiState.value = TransactionUiState.Success(transactions)
                    }.onFailure { exception ->
                        // Ini akan menangani Result.failure() jika Anda memutuskan untuk meng-emit-nya dari repo
                        _uiState.value = TransactionUiState.Error(exception.message ?: "Failed to load transactions")
                    }
                }
        }
    }

    companion object {
        fun provideFactory(
            repository: TransactionRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(TransactionHistoryViewModel::class.java)) {
                    return TransactionHistoryViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}