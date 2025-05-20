package com.cognifyteam.cognifyapp.data.sources.remote.services

import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.data.models.UserJson
import retrofit2.http.POST

interface AuthService {
    @POST("auth/register")
    suspend fun register(user: UserJson): User
}