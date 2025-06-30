package com.cognifyteam.cognifyapp.data.sources.remote.services

import com.cognifyteam.cognifyapp.data.models.Course
import com.cognifyteam.cognifyapp.data.sources.remote.BaseResponse
import com.cognifyteam.cognifyapp.data.sources.remote.EnrolledCoursesData
import com.cognifyteam.cognifyapp.data.models.CourseJson
import com.cognifyteam.cognifyapp.data.models.DiscussionJson
import com.cognifyteam.cognifyapp.data.models.UserCoursesDataWrapper

import com.cognifyteam.cognifyapp.data.sources.remote.ApiResponse
import com.cognifyteam.cognifyapp.data.sources.remote.CourseDataWrapper
import com.cognifyteam.cognifyapp.data.sources.remote.CreateCourseRequest
import com.cognifyteam.cognifyapp.data.sources.remote.CreatePaymentRequest
import com.cognifyteam.cognifyapp.data.sources.remote.CreateReplyRequest
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import okhttp3.RequestBody
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class PaymentTokenResponse(
    @Json(name = "token")
    val token: String,
    @Json(name = "redirect_url")
    val redirect_url: String
)

interface CourseService {
    @GET("course/getUserCourse/{firebaseId}")
    suspend fun getEnrolledCourses(
        @Path("firebaseId") firebaseId: String,
        @Query("q") query: String? // Dibuat nullable, Retrofit akan mengabaikannya jika null
    ): BaseResponse<EnrolledCoursesData>

    @GET("course/{courseId}") // Sesuaikan dengan path route di backend Anda
    suspend fun getCourseById(
        @Path("courseId") courseId: String
    ): BaseResponse<CourseDataWrapper>

    @Multipart
    @POST("course/createCourse")
    suspend fun createCourse(
        @Part thumbnail: MultipartBody.Part,
        @Part("course_name") course_name: RequestBody,
        @Part("course_description") course_description: RequestBody,
        @Part("course_owner") course_owner: RequestBody,
        @Part("course_price") course_price: RequestBody,
        @Part("category_id") category_id: RequestBody,
        @Part("course_owner_name") course_owner_name: RequestBody
    ): BaseResponse<CourseDataWrapper>

    @GET("course/courses/{firebaseId} ")
    suspend fun getUserCreatedCourses(
        @Path("firebaseId") userId: String
    ): BaseResponse<UserCoursesDataWrapper>

    @GET("course/search")
    suspend fun getCourses(
        @Query("sortBy") sortBy: String
    ): BaseResponse<EnrolledCoursesData>

    @GET("course/getAllCourse")
    suspend fun getAllCourses(
        @Query("q") query: String? // Dibuat nullable, Retrofit akan mengabaikannya jika null
    ): BaseResponse<EnrolledCoursesData>
    
    @POST("course/{courseId}/payment")
    suspend fun createPayment(
        @Path("courseId") courseId: String,
        @Body request: CreatePaymentRequest
    ): BaseResponse<PaymentTokenResponse>
}