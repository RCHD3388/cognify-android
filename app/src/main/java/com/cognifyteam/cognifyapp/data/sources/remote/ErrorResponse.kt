package com.cognifyteam.cognifyapp.data.sources.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    val status: String,
    val message: String,
    val errors: List<String>,
    val stack: String?
)