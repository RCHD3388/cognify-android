package com.cognifyteam.cognifyapp.data.sources.remote.services

import com.cognifyteam.cognifyapp.data.models.UserJson
import com.cognifyteam.cognifyapp.data.sources.remote.BaseResponse
import com.cognifyteam.cognifyapp.data.sources.remote.GenericSuccessData
import com.cognifyteam.cognifyapp.data.sources.remote.UserSearchData
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FollowService {
    /**
     * Mengambil daftar user yang DI-FOLLOW oleh user dengan ID tertentu.
     * Endpoint: GET /api/v1/users/:userId/following
     */
    @GET("users/{userId}/following")
    suspend fun getFollowing(
        @Path("userId") userId: String
    ): BaseResponse<List<UserJson>> // API mengembalikan daftar user

    /**
     * Mengambil daftar user yang MENGIKUTI (followers) user dengan ID tertentu.
     * Endpoint: GET /api/v1/users/:userId/followers
     */
    @GET("users/{userId}/followers")
    suspend fun getFollowers(
        @Path("userId") userId: String
    ): BaseResponse<List<UserJson>> // API juga mengembalikan daftar user

    /**
     * Aksi untuk user yang sedang login untuk mengikuti user lain.
     * Endpoint: POST /api/v1/users/:userIdToFollow/follow
     */
    @POST("users/{userIdToFollow}/follow")
    suspend fun followUser(
        @Path("userIdToFollow") userIdToFollow: String
    ): BaseResponse<GenericSuccessData> // API mengembalikan pesan sukses

    /**
     * Aksi untuk user yang sedang login untuk berhenti mengikuti user lain.
     * Endpoint: DELETE /api/v1/users/:userIdToUnfollow/unfollow
     */
    @DELETE("users/{userIdToUnfollow}/unfollow")
    suspend fun unfollowUser(
        @Path("userIdToUnfollow") userIdToUnfollow: String
    ): BaseResponse<GenericSuccessData> // API juga mengembalikan pesan sukses

    @GET("users/search")
    suspend fun searchUsers(
        @Query("q") query: String
    ): BaseResponse<UserSearchData>
}