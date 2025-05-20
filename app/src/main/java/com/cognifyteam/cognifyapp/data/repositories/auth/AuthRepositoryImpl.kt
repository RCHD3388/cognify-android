package com.cognifyteam.cognifyapp.data.repositories.auth

import android.util.Log
import com.cognifyteam.cognifyapp.data.sources.local.datasources.LocalAuthDataSource
import com.cognifyteam.cognifyapp.data.sources.remote.auth.RemoteAuthDataSource

class AuthRepositoryImpl(
    private val localAuthDataSource: LocalAuthDataSource,
    private val remoteAuthDataSource: RemoteAuthDataSource
): AuthRepository {
    override suspend fun register(firebaseId: String, name: String, email: String): Result<Unit> {
        Log.d("asd", "acong jancok")
        return Result.success(Unit)
    }
}