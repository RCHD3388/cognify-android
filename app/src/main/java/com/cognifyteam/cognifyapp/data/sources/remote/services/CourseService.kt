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
import com.cognifyteam.cognifyapp.data.sources.remote.CreateReplyRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import okhttp3.RequestBody
interface CourseService {
    @GET("course/getUserCourse/{firebaseId}")
    suspend fun getEnrolledCourses(@Path("firebaseId") firebaseId: String): BaseResponse<EnrolledCoursesData>

    @Multipart
    @POST("course/createCourse")
    suspend fun createCourse(
        @Part thumbnail: MultipartBody.Part,
        @Part("course_name") course_name: RequestBody,
        @Part("course_description") course_description: RequestBody,
        @Part("course_owner") course_owner: RequestBody,
        @Part("course_price") course_price: RequestBody,
        @Part("category_id") category_id: RequestBody
    ): BaseResponse<CourseDataWrapper>

    @GET("course/courses/{firebaseId} ")
    suspend fun getUserCreatedCourses(
        @Path("firebaseId") userId: String
    ): BaseResponse<UserCoursesDataWrapper>
}