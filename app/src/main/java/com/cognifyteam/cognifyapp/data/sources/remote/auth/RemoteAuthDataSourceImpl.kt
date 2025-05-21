package com.cognifyteam.cognifyapp.data.sources.remote.auth

import android.util.Log
import com.cognifyteam.cognifyapp.data.AppContainer
import com.cognifyteam.cognifyapp.data.AppContainerImpl
import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.data.sources.remote.ApiResponse
import com.cognifyteam.cognifyapp.data.sources.remote.services.AuthService

class RemoteAuthDataSourceImpl(
    private val authService: AuthService
): RemoteAuthDataSource {
    override suspend fun register(user: User): Result<User> {
        try {
            Log.d("asd", "asd1")
            val response = authService.register(user.toJson())
            Log.d("asd", "asd2")
            if(response.isSuccessful){
                Log.d("berhasil", "berhasil")
                val body = response.body()
                if (body != null){
                    return Result.success(body.data.data);
                }else{
                    return Result.failure(Exception("Empty response"))
                }
            } else {
                val error = AppContainerImpl.parseErrorMessage(response.errorBody()?.string())
                Log.d("asd", "${error}")
                return Result.failure(Exception(error ?: "Unknown error"))
            }
        } catch (e: Exception){
            return Result.failure(e)
        }
    }
}