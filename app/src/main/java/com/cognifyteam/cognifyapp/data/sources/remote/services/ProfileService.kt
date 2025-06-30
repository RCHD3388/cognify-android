package com.cognifyteam.cognifyapp.data.sources.remote.services

import com.cognifyteam.cognifyapp.data.models.ApiResponse
import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.data.models.UserJson
import com.cognifyteam.cognifyapp.data.sources.remote.BaseResponse
import com.cognifyteam.cognifyapp.data.sources.remote.ProfileData
import com.cognifyteam.cognifyapp.data.sources.remote.UpdateProfileRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface ProfileService {
    @GET("profile/getprofile/{firebaseId}")
    suspend fun getProfile(
        @Path("firebaseId") firebaseId: String
    ): BaseResponse<ProfileData>
    @PATCH("profile/updateMyProfile/{firebaseId}")
    suspend fun updateProfile(
        @Path("firebaseId") firebaseId: String,
        @Body request: UpdateProfileRequest
    ): Response<ApiResponse<UserJson>>
}