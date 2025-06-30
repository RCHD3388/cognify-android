package com.cognifyteam.cognifyapp.data.sources.remote

import com.cognifyteam.cognifyapp.data.models.CourseJson
import com.cognifyteam.cognifyapp.data.models.DiscussionJson
import com.cognifyteam.cognifyapp.data.models.RatingJson
import com.cognifyteam.cognifyapp.data.models.User
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
data class EnrollmentCheckResponse(
    @Json(name = "isEnrolled")
    val isEnrolled: Boolean
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

@JsonClass(generateAdapter = true)
data class DiscussionListData(
    @Json(name = "message") val message: String,
    @Json(name = "data") val discussions: List<DiscussionJson>
)
data class CourseDataWrapper(
    val message: String,
    val data: CourseJson
)

// DTO untuk mengirim data saat membuat post utama
@JsonClass(generateAdapter = true)
data class CreatePostRequest(
    @Json(name = "content")
    val content: String,

    @Json(name = "courseId")
    val courseId: String
)

@JsonClass(generateAdapter = true)
data class CreatePaymentRequest(
    val firebaseId: String
)

// DTO untuk mengirim data saat membuat balasan
@JsonClass(generateAdapter = true)
data class CreateReplyRequest(
    @Json(name = "content")
    val content: String
)

@JsonClass(generateAdapter = true)
data class CreateCourseRequest(
    @Json(name = "course_name")
    val course_name: String,
    @Json(name = "course_description")
    val course_description: String,
    @Json(name = "course_owner")
    val course_owner: String,
    @Json(name = "course_price")
    val course_price: Int,
    @Json(name = "category_id")
    val category_id: String
)

@JsonClass(generateAdapter = true)
data class GenericSuccessData(
    @Json(name = "message")
    val message: String
)

data class UserUiState(
    val user: User,
    val isFollowing: Boolean = false
)

@JsonClass(generateAdapter = true)
data class UpdateProfileRequest(
    @Json(name = "name")
    val name: String?,

    @Json(name = "description")
    val description: String?
)

@JsonClass(generateAdapter = true)
data class UserSearchData(
    @Json(name = "message")
    val message: String,

    @Json(name = "data")
    val users: List<UserJson>
)

/**
 * DTO untuk request body saat user mengirim rating baru.
 */
@JsonClass(generateAdapter = true)
data class PostRatingRequest(
    @Json(name = "rating")
    val rating: Int,

    @Json(name = "comment")
    val comment: String?, // Dibuat nullable karena opsional

    @Json(name = "firebaseId")
    val firebaseId: String
)

/**
 * DTO untuk data yang ada di dalam `BaseResponse` saat mengambil daftar rating.
 */
@JsonClass(generateAdapter = true)
data class RatingListData(
    // Backend tidak mengirim 'message' di sini, hanya 'data' array
    @Json(name = "data")
    val ratings: List<RatingJson>
)
