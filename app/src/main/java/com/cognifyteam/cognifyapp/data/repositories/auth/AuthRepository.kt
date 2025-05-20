package com.cognifyteam.cognifyapp.data.repositories.auth

interface AuthRepository {
    suspend fun register(name: String, email: String, password: String): Result<Unit>
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun logout(): Result<Unit>
}