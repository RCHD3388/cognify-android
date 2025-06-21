package com.cognifyteam.cognifyapp.data.remote.services

import com.cognifyteam.cognifyapp.data.remote.responses.BaseResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ProfileService {
    @GET("api/v1/profile/getprofile/{firebaseId}")
    suspend fun getProfile(
        @Path("firebaseId") firebaseId: String
    ): BaseResponse
}