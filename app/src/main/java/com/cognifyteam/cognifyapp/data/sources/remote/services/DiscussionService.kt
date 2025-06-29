package com.cognifyteam.cognifyapp.data.sources.remote.services

import com.cognifyteam.cognifyapp.data.models.ApiResponse
import com.cognifyteam.cognifyapp.data.models.DiscussionJson
import com.cognifyteam.cognifyapp.data.sources.remote.BaseResponse
import com.cognifyteam.cognifyapp.data.sources.remote.CreatePostRequest
import com.cognifyteam.cognifyapp.data.sources.remote.CreateReplyRequest
import com.cognifyteam.cognifyapp.data.sources.remote.DiscussionListData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DiscussionService {
    @GET("course/discussion/{courseId}")
    suspend fun getDiscussionsForCourse(
        @Path("courseId") courseId: String
    ): BaseResponse<DiscussionListData>

    @POST("course/discussion/{firebaseId}")
    suspend fun createDiscussionPost(
        @Path("firebaseId") firebaseId: String,
        @Body request: CreatePostRequest
    ): Response<ApiResponse<DiscussionJson>>

    @POST("course/discussion/{parentId}/reply/{firebaseId}")
    suspend fun createReply(
        @Path("parentId") parentId: Int,
        @Path("firebaseId") firebaseId: String,
        @Body request: CreateReplyRequest
    ): Response<ApiResponse<DiscussionJson>>
}