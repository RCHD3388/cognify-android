package com.cognifyteam.cognifyapp.data.sources.remote.auth

import android.util.Log
import com.cognifyteam.cognifyapp.data.AppContainer
import com.cognifyteam.cognifyapp.data.AppContainerImpl
import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.data.models.UserJson
import com.cognifyteam.cognifyapp.data.sources.remote.ApiResponse
import com.cognifyteam.cognifyapp.data.sources.remote.services.AuthService

interface RemoteAuthDataSource {
    suspend fun register(user: UserJson): Result<User>
    suspend fun login(firebaseId: String): Result<User>
}

class RemoteAuthDataSourceImpl(
    private val authService: AuthService
): RemoteAuthDataSource {
    override suspend fun register(user: UserJson): Result<User> {
        try {
            val response = authService.register(user)
            if(response.isSuccessful){
                val body = response.body()
                if (body != null){
                    return Result.success(body.data.data);
                }else{
                    return Result.failure(Exception("Empty response"))
                }
            } else {
                val error = AppContainerImpl.parseErrorMessage(response.errorBody()?.string())
                return Result.failure(Exception(error ?: "Unknown error"))
            }
        } catch (e: Exception){
            return Result.failure(e)
        }
    }
    override suspend fun login(firebaseId: String): Result<User> {
        try {
            val response = authService.login(firebaseId)
            if(response.isSuccessful){
                val body = response.body()
                if (body != null){
                    return Result.success(body.data.data);
                }else{
                    return Result.failure(Exception("Empty response"))
                }
            } else {
                val error = AppContainerImpl.parseErrorMessage(response.errorBody()?.string())
                return Result.failure(Exception(error ?: "Unknown error"))
            }
        } catch (e: Exception){
            return Result.failure(e)
        }
    }
}