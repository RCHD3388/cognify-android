package com.cognifyteam.cognifyapp.data.sources.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiDataResponse<T>(
    val message: String,
    val data: T,
)

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    val status: String,
    val code: Int,
    val data: ApiDataResponse<T>
)