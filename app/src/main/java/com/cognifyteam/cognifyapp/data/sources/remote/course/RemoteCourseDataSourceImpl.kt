package com.cognifyteam.cognifyapp.data.sources.remote.course

import com.cognifyteam.cognifyapp.data.AppContainerImpl
import com.cognifyteam.cognifyapp.data.models.Course
import com.cognifyteam.cognifyapp.data.sources.remote.ApiResponse
import com.cognifyteam.cognifyapp.data.sources.remote.BaseResponse
import com.cognifyteam.cognifyapp.data.sources.remote.EnrolledCoursesData
import com.cognifyteam.cognifyapp.data.sources.remote.services.CourseService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

interface RemoteCourseDataSource {
    suspend fun getEnrolledCourses(firebaseId: String): BaseResponse<EnrolledCoursesData>
    suspend fun createCourse(
        thumbnail: MultipartBody.Part,
        courseData: Map<String, RequestBody> // Kita buat lebih simpel dengan Map
    ): Result<Course>
}

class RemoteCourseDataSourceImpl(
    private val courseService: CourseService
) : RemoteCourseDataSource {

    override suspend fun getEnrolledCourses(firebaseId: String): BaseResponse<EnrolledCoursesData> {
        return courseService.getEnrolledCourses(firebaseId)
    }
    override suspend fun createCourse(
        thumbnail: MultipartBody.Part,
        courseData: Map<String, RequestBody>
    ): Result<Course> {
        try {
            val response = courseService.createCourse(
                thumbnail = thumbnail,
                courseName = courseData["course_name"]!!,
                courseDescription = courseData["course_description"]!!,
                courseOwner = courseData["course_owner"]!!,
                coursePrice = courseData["course_price"]!!,
                categoryId = courseData["category_id"]!!
            )
            // ... sisa logika try-catch Anda yang sudah benar ...
            if (response.isSuccessful) {
                val body = response.body()?.data
                if (body?.data != null) {
                    return Result.success(body.data)
                } else {
                    return Result.failure(Exception("Nested course data is null"))
                }
            } else {
                val errorMsg = AppContainerImpl.parseErrorMessage(response.errorBody()?.string())
                return Result.failure(Exception(errorMsg ?: "Failed to create course"))
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

}