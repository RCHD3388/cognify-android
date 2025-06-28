// Berkas: data/sources/remote/course/RemoteCourseDataSource.kt

package com.cognifyteam.cognifyapp.data.sources.remote.course

import com.cognifyteam.cognifyapp.data.models.CourseJson
import com.cognifyteam.cognifyapp.data.models.UserCoursesDataWrapper
import com.cognifyteam.cognifyapp.data.sources.remote.BaseResponse
import com.cognifyteam.cognifyapp.data.sources.remote.CourseDataWrapper
import com.cognifyteam.cognifyapp.data.sources.remote.EnrolledCoursesData
import com.cognifyteam.cognifyapp.data.sources.remote.services.CourseService
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface RemoteCourseDataSource {
    // --- FUNGSI BARU ---
    suspend fun getCourseById(courseId: String): BaseResponse<CourseDataWrapper>

    suspend fun getEnrolledCourses(firebaseId: String, query: String?): BaseResponse<EnrolledCoursesData>
    suspend fun createCourse(
        thumbnail: MultipartBody.Part,
        course_name: RequestBody,
        course_description: RequestBody,
        course_owner: RequestBody,
        course_price: RequestBody,
        category_id: RequestBody
    ): BaseResponse<CourseDataWrapper>

    suspend fun getUserCreatedCourses(firebaseId: String): BaseResponse<UserCoursesDataWrapper>
}

class RemoteCourseDataSourceImpl(
    private val courseService: CourseService
) : RemoteCourseDataSource {

    override suspend fun getCourseById(courseId: String): BaseResponse<CourseDataWrapper> {
        return courseService.getCourseById(courseId)
    }

    override suspend fun getEnrolledCourses(firebaseId: String, query: String?): BaseResponse<EnrolledCoursesData> {
        return courseService.getEnrolledCourses(firebaseId, query)
    }
    override suspend fun createCourse(
        thumbnail: MultipartBody.Part,
        courseName: RequestBody,
        courseDescription: RequestBody,
        courseOwner: RequestBody,
        coursePrice: RequestBody,
        categoryId: RequestBody
    ): BaseResponse<CourseDataWrapper> {

        return courseService.createCourse(
            thumbnail = thumbnail,
            course_name = courseName,
            course_description = courseDescription,
            course_owner = courseOwner,
            course_price = coursePrice,
            category_id = categoryId
        )
    }

    override suspend fun getUserCreatedCourses(firebaseId: String): BaseResponse<UserCoursesDataWrapper> {
        return courseService.getUserCreatedCourses(firebaseId)
    }
}