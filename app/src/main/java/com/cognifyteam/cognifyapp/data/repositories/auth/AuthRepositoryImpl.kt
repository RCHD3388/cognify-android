package com.cognifyteam.cognifyapp.data.repositories.auth

import android.util.Log

class AuthRepositoryImpl: AuthRepository {
    override suspend fun register(name: String, email: String, password: String): Result<Unit> {
        return Result.success(Unit)
    }
    override suspend fun login(email: String, password: String): Result<Unit> {
        Log.d("testing repository", "login: called");
        return Result.success(Unit)
    }
    override suspend fun logout(): Result<Unit> {
        return Result.success(Unit)
    }
}