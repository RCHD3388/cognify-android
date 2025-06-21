package com.cognifyteam.cognifyapp.data.sources.remote.profile

import com.cognifyteam.cognifyapp.data.remote.responses.BaseResponse
import com.cognifyteam.cognifyapp.data.remote.services.ProfileService

class RemoteProfileDataSourceImpl(
    private val profileService: ProfileService
) : RemoteProfileDataSource {
    override suspend fun getProfile(firebaseId: String): BaseResponse     {
        return profileService.getProfile(firebaseId)
    }
}