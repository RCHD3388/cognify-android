package com.cognifyteam.cognifyapp.data.sources.remote.services

import com.cognifyteam.cognifyapp.data.models.CommentBody
import com.cognifyteam.cognifyapp.data.models.GenerateLearningPathPayloadJson
import com.cognifyteam.cognifyapp.data.models.GeneratedLearningPath
import com.cognifyteam.cognifyapp.data.models.LearningPath
import com.cognifyteam.cognifyapp.data.models.LikedBody
import com.cognifyteam.cognifyapp.data.models.LikedRes
import com.cognifyteam.cognifyapp.data.models.SaveLearningPathPayload
import com.cognifyteam.cognifyapp.data.models.SmartComment
import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.data.models.UserJson
import com.cognifyteam.cognifyapp.data.sources.remote.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SmartService {
    @POST("smart/new")
    suspend fun generate(@Body body: GenerateLearningPathPayloadJson): Response<ApiResponse<GeneratedLearningPath>>
    @POST("smart/")
    suspend fun save(@Body body: SaveLearningPathPayload): Response<ApiResponse<LearningPath>>
    @GET("smart/")
    suspend fun getAll(): Response<ApiResponse<List<LearningPath>>>
    @POST("smart/like/{smartId}")
    suspend fun likeSmart( @Path("smartId") smartId: Int, @Body body: LikedBody): Response<ApiResponse<LikedRes>>
    @POST("smart/comment/{smartId}")
    suspend fun addComment( @Path("smartId") smartId: Int, @Body body: CommentBody): Response<ApiResponse<SmartComment>>
}