package com.cognifyteam.cognifyapp.data.sources.remote.services

import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.data.remote.responses.UserDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/register") // Ganti dengan endpoint register Anda
    suspend fun register(
        @Body user: UserDto // <-- Terima UserDto sebagai Body
    ): User
}