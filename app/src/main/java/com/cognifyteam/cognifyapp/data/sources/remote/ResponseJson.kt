package com.cognifyteam.cognifyapp.data.sources.remote

import com.cognifyteam.cognifyapp.data.models.CourseJson
import com.cognifyteam.cognifyapp.data.models.UserJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiDataResponse<T>(
    val message: String,
    val data: T,
)

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    val status: String,
    val code: Int,
    val data: ApiDataResponse<T>
)

@JsonClass(generateAdapter = true)
data class ProfileData(
    @Json(name = "message")
    val message: String,

    @Json(name = "data")
    val data: UserJson
)

@JsonClass(generateAdapter = true)
data class BaseResponse<T>(
    @Json(name = "status")
    val status: String,

    @Json(name = "code")
    val code: Int,

    @Json(name = "data")
    val data: T
)

@JsonClass(generateAdapter = true)
data class EnrolledCoursesData(
    @Json(name = "message")
    val message: String,

    @Json(name = "data")
    val courses: List<CourseJson>
)