package com.cognifyteam.cognifyapp.data.sources.remote.course

import com.cognifyteam.cognifyapp.data.models.ApiResponse
import com.cognifyteam.cognifyapp.data.models.DiscussionJson
import com.cognifyteam.cognifyapp.data.sources.remote.BaseResponse
import com.cognifyteam.cognifyapp.data.sources.remote.CreatePostRequest
import com.cognifyteam.cognifyapp.data.sources.remote.CreateReplyRequest
import com.cognifyteam.cognifyapp.data.sources.remote.DiscussionListData
import com.cognifyteam.cognifyapp.data.sources.remote.services.DiscussionService
import retrofit2.Response

interface RemoteDiscussionDataSource {
    suspend fun getDiscussionsForCourse(courseId: String): BaseResponse<DiscussionListData>
    suspend fun createPost(firebaseId: String, request: CreatePostRequest): Response<ApiResponse<DiscussionJson>>
    suspend fun createReply(parentId: Int, firebaseId: String, request: CreateReplyRequest): Response<ApiResponse<DiscussionJson>>
}

class RemoteDiscussionDataSourceImpl(
    private val discussionService: DiscussionService
) : RemoteDiscussionDataSource {

    override suspend fun getDiscussionsForCourse(courseId: String): BaseResponse<DiscussionListData> {
        return discussionService.getDiscussionsForCourse(courseId)
    }

    override suspend fun createPost(
        firebaseId: String,
        request: CreatePostRequest
    ): Response<ApiResponse<DiscussionJson>> {
        return discussionService.createDiscussionPost(firebaseId, request)
    }

    override suspend fun createReply(
        parentId: Int,
        firebaseId: String,
        request: CreateReplyRequest
    ): Response<ApiResponse<DiscussionJson>> {
        return discussionService.createReply(parentId, firebaseId, request)
    }

}