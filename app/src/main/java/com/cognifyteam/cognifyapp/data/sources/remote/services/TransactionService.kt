package com.cognifyteam.cognifyapp.data.sources.remote.services

import com.cognifyteam.cognifyapp.data.models.TransactionJson
import com.cognifyteam.cognifyapp.data.sources.remote.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface TransactionService {
    @GET("transaction/{userId}")
    suspend fun getUserTransactions(
        @Path("userId") userId: String
    ): ApiResponse<List<TransactionJson>>
}