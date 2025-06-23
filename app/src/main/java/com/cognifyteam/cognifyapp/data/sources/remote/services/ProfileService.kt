package com.cognifyteam.cognifyapp.data.sources.remote.services

import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.data.sources.remote.BaseResponse
import com.cognifyteam.cognifyapp.data.sources.remote.ProfileData
import retrofit2.http.GET
import retrofit2.http.Path

interface ProfileService {
    @GET("api/v1/profile/getprofile/{firebaseId}")
    suspend fun getProfile(
        @Path("firebaseId") firebaseId: String
    ): BaseResponse<ProfileData>
}