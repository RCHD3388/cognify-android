package com.cognifyteam.cognifyapp.data.repositories.auth

import android.util.Log
import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.data.models.UserJson
import com.cognifyteam.cognifyapp.data.sources.local.datasources.LocalAuthDataSource
import com.cognifyteam.cognifyapp.data.sources.remote.auth.RemoteAuthDataSource

interface AuthRepository {
    suspend fun register(firebaseId: String, name: String, email: String, role: String): Result<User>
    suspend fun login(firebaseId: String): Result<User>
}

class AuthRepositoryImpl(
    private val localAuthDataSource: LocalAuthDataSource,
    private val remoteAuthDataSource: RemoteAuthDataSource
): AuthRepository {
    override suspend fun register(firebaseId: String, name: String, email: String, role: String): Result<User> {
        val result = remoteAuthDataSource.register(UserJson(firebaseId, name, email, role));
        result.onSuccess {
            localAuthDataSource.register(it)
        }
        return result
    }

    override suspend fun login(firebaseId: String): Result<User>{
        val result = remoteAuthDataSource.login(firebaseId);
        result.onSuccess {
            localAuthDataSource.insertOrReplace(it)
        }
        return result
    }
}