package com.cognifyteam.cognifyapp.data.sources.remote.services

import com.cognifyteam.cognifyapp.data.models.RatingJson
import com.cognifyteam.cognifyapp.data.sources.remote.BaseResponse
import com.cognifyteam.cognifyapp.data.sources.remote.PostRatingRequest
import com.cognifyteam.cognifyapp.data.sources.remote.RatingListData
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RatingService {

    /**
     * Mengambil semua rating untuk sebuah kursus.
     * Endpoint: GET /api/v1/courses/:courseId/ratings
     */
    @GET("course/{courseId}/ratings")
    suspend fun getRatings(
        @Path("courseId") courseId: String
    ): BaseResponse<RatingListData> // Menggunakan DTO pembungkus yang sesuai

    /**
     * Mengirim rating baru untuk sebuah kursus.
     * Endpoint: POST /api/v1/courses/:courseId/ratings
     */
    @POST("course/{courseId}/ratings")
    suspend fun postRating(
        @Path("courseId") courseId: String,
        @Body request: PostRatingRequest
    ): BaseResponse<Any> // Kita tidak terlalu peduli dengan respons suksesnya, jadi 'Any' sudah cukup

    @GET("course/{courseId}/my-rating/{firebaseId}")
    suspend fun getMyRating(
        @Path("courseId") courseId: String,
        @Path("firebaseId") firebaseId: String
    ): BaseResponse<RatingJson?> // <-- Kembalikan RatingJson yang nullable!
}