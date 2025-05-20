package com.cognifyteam.cognifyapp.data.sources.local.datasources

import com.cognifyteam.cognifyapp.data.models.User

interface LocalAuthDataSource {
    suspend fun register(firebaseId: String, name: String, email: String): User
}