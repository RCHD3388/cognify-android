package com.cognifyteam.cognifyapp.data.sources.remote.auth

import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.data.sources.remote.ApiResponse

interface RemoteAuthDataSource {
    suspend fun register(user: User): Result<User>
}