package com.cognifyteam.cognifyapp.data.sources.remote.users

import com.cognifyteam.cognifyapp.data.models.UserJson
import com.cognifyteam.cognifyapp.data.sources.remote.BaseResponse
import com.cognifyteam.cognifyapp.data.sources.remote.GenericSuccessData
import com.cognifyteam.cognifyapp.data.sources.remote.UserSearchData
import com.cognifyteam.cognifyapp.data.sources.remote.services.FollowService

interface RemoteFollowDataSource {
    suspend fun getFollowing(userId: String): BaseResponse<List<UserJson>>
    suspend fun getFollowers(userId: String): BaseResponse<List<UserJson>>
    suspend fun followUser(userIdToFollow: String, firebaseId: String): BaseResponse<GenericSuccessData>
    suspend fun unfollowUser(userIdToUnfollow: String, firebaseId: String): BaseResponse<GenericSuccessData>
    suspend fun searchUsers(query: String): BaseResponse<UserSearchData>
}

class RemoteFollowDataSourceImpl(
    private val followService: FollowService
) : RemoteFollowDataSource {

    override suspend fun getFollowing(userId: String): BaseResponse<List<UserJson>> {
        return followService.getFollowing(userId)
    }

    override suspend fun getFollowers(userId: String): BaseResponse<List<UserJson>> {
        return followService.getFollowers(userId)
    }

    override suspend fun followUser(userIdToFollow: String, firebaseId: String): BaseResponse<GenericSuccessData> {
        return followService.followUser(userIdToFollow, firebaseId)
    }

    override suspend fun unfollowUser(userIdToUnfollow: String, firebaseId: String): BaseResponse<GenericSuccessData> {
        return followService.unfollowUser(userIdToUnfollow, firebaseId)
    }

    override suspend fun searchUsers(query: String): BaseResponse<UserSearchData> {
        return followService.searchUsers(query)
    }
}