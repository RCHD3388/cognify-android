package com.cognifyteam.cognifyapp.data.repositories.auth

import android.util.Log
import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.data.sources.local.datasources.LocalAuthDataSource
import com.cognifyteam.cognifyapp.data.sources.remote.auth.RemoteAuthDataSource

class AuthRepositoryImpl(
    private val localAuthDataSource: LocalAuthDataSource,
    private val remoteAuthDataSource: RemoteAuthDataSource
): AuthRepository {
    override suspend fun register(firebaseId: String, name: String, email: String): Result<User> {
        var user = User(firebaseId, name, email);
        Log.d("asd", "asdasdasdasd")
        val result = remoteAuthDataSource.register(user);
        if(result.isSuccess){
            localAuthDataSource.register(firebaseId, name, email);
        }
        return result
    }
}