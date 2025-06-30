package com.cognifyteam.cognifyapp.data.sources.remote.transaction


import com.cognifyteam.cognifyapp.data.models.TransactionJson
import com.cognifyteam.cognifyapp.data.sources.remote.ApiResponse
import com.cognifyteam.cognifyapp.data.sources.remote.services.TransactionService

interface RemoteTransactionDataSource {
    suspend fun getUserTransactions(userId: String): ApiResponse<List<TransactionJson>>
}

class RemoteTransactionDataSourceImpl(
    private val apiService: TransactionService
) : RemoteTransactionDataSource {

    override suspend fun getUserTransactions(userId: String): ApiResponse<List<TransactionJson>> {
        // Tugasnya hanya mendelegasikan panggilan ke ApiService.
        return apiService.getUserTransactions(userId)
    }
}