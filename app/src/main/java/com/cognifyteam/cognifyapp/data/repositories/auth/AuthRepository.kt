package com.cognifyteam.cognifyapp.data.repositories.auth

interface AuthRepository {
    suspend fun register(firebaseId: String, name: String, email: String): Result<Unit>
}