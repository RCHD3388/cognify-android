package com.cognifyteam.cognifyapp.data.sources.remote.services

import com.cognifyteam.cognifyapp.data.sources.remote.BaseResponse
import com.cognifyteam.cognifyapp.data.sources.remote.EnrolledCoursesData
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface CourseService {
    @GET("course/getUserCourse/{firebaseId}")
    suspend fun getEnrolledCourses(@Path("firebaseId") firebaseId: String): BaseResponse<EnrolledCoursesData>

    @Multipart
    @POST("course/createCourse")
    suspend fun createCourse(@Part thumbnail : MultipartBody.Part):String
}