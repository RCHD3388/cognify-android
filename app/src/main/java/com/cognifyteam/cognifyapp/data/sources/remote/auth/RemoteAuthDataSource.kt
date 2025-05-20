package com.cognifyteam.cognifyapp.data.sources.remote.auth

import com.cognifyteam.cognifyapp.data.models.User

interface RemoteAuthDataSource {
    suspend fun register(user: User): User
}