package com.cognifyteam.cognifyapp.data.repositories.auth

import com.cognifyteam.cognifyapp.data.models.User

interface AuthRepository {
    suspend fun register(firebaseId: String, name: String, email: String): Result<User>
}