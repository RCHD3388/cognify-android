package com.cognifyteam.cognifyapp.data.sources.remote.services

import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.data.models.UserJson
import com.cognifyteam.cognifyapp.data.sources.remote.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthService {
    @POST("auth/register")
    suspend fun register(@Body body: UserJson): Response<ApiResponse<User>>
    @GET("auth/login/{firebaseId}")
    suspend fun login(@Path("firebaseId") firebaseId: String): Response<ApiResponse<User>>
}