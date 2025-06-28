package com.cognifyteam.cognifyapp.data.sources.remote.services

import com.cognifyteam.cognifyapp.data.models.Course
import com.cognifyteam.cognifyapp.data.sources.remote.BaseResponse
import com.cognifyteam.cognifyapp.data.sources.remote.EnrolledCoursesData
import com.cognifyteam.cognifyapp.data.models.CourseJson
import com.cognifyteam.cognifyapp.data.sources.remote.ApiResponse
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

interface CourseService {
    @GET("course/getUserCourse/{firebaseId}")
    suspend fun getEnrolledCourses(
        @Path("firebaseId") firebaseId: String,
        @Query("q") query: String? // Dibuat nullable, Retrofit akan mengabaikannya jika null
    ): BaseResponse<EnrolledCoursesData>

    @Multipart
    @POST("course/createCourse")
    suspend fun createCourse(
        @Part thumbnail: MultipartBody.Part,
        @Part("course_name") courseName: RequestBody,
        @Part("course_description") courseDescription: RequestBody,
        @Part("course_owner") courseOwner: RequestBody,
        @Part("course_price") coursePrice: RequestBody,
        @Part("category_id") categoryId: RequestBody
    ): Response<ApiResponse<Course>>
}