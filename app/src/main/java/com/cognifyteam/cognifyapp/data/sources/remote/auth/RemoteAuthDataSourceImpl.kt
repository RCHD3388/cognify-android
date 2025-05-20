package com.cognifyteam.cognifyapp.data.sources.remote.auth

import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.data.sources.remote.services.AuthService

class RemoteAuthDataSourceImpl(
    private val authService: AuthService
): RemoteAuthDataSource {
    override suspend fun register(user: User): User {
        TODO("Not yet implemented")
    }
}