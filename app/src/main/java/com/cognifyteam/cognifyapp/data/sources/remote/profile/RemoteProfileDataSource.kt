package com.cognifyteam.cognifyapp.data.sources.remote.profile

import com.cognifyteam.cognifyapp.data.remote.responses.BaseResponse

interface RemoteProfileDataSource {
    suspend fun getProfile(firebaseId: String): BaseResponse
}