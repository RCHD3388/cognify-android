package com.cognifyteam.cognifyapp.data.sources.remote.services

import com.cognifyteam.cognifyapp.data.models.CreateMultipleSectionsRequest
import com.cognifyteam.cognifyapp.data.models.Section
import com.cognifyteam.cognifyapp.data.sources.remote.ApiDataResponse
import com.cognifyteam.cognifyapp.data.sources.remote.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
interface SectionService {

    /**
     * Mengambil semua section untuk course tertentu.
     * Endpoint: GET /courses/{course_id}/sections
     */
    @GET("courses/{course_id}/sections")
    suspend fun getSectionsByCourse(
        @Path("course_id") courseId: String
    ): ApiResponse<ApiDataResponse<List<Section>>>


    @POST("courses/{course_id}/sections")
    suspend fun createMultipleSections(
        @Path("course_id") courseId: String,
        @Body requestBody: CreateMultipleSectionsRequest
    ): ApiResponse<ApiDataResponse<List<Section>>>
}