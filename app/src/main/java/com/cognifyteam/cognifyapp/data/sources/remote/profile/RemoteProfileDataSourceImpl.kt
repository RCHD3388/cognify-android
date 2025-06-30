package com.cognifyteam.cognifyapp.data.sources.remote.profile

import com.cognifyteam.cognifyapp.data.models.ApiResponse
import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.data.models.UserJson
import com.cognifyteam.cognifyapp.data.sources.remote.BaseResponse
import com.cognifyteam.cognifyapp.data.sources.remote.DiscussionListData
import com.cognifyteam.cognifyapp.data.sources.remote.ProfileData
import com.cognifyteam.cognifyapp.data.sources.remote.UpdateProfileRequest
import com.cognifyteam.cognifyapp.data.sources.remote.services.ProfileService
import retrofit2.Response

interface RemoteProfileDataSource {
    suspend fun getProfile(firebaseId: String): BaseResponse<ProfileData>
    suspend fun updateProfile(firebaseId: String, request: UpdateProfileRequest): Response<ApiResponse<UserJson>>
}

class RemoteProfileDataSourceImpl(
    private val profileService: ProfileService
) : RemoteProfileDataSource {
    override suspend fun getProfile(firebaseId: String): BaseResponse<ProfileData> {
        return profileService.getProfile(firebaseId)
    }
    override suspend fun updateProfile(firebaseId: String, request: UpdateProfileRequest): Response<ApiResponse<UserJson>> {
        return profileService.updateProfile(firebaseId, request)
    }
}