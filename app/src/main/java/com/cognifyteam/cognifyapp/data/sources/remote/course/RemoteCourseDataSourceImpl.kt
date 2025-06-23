package com.cognifyteam.cognifyapp.data.sources.remote.course

import com.cognifyteam.cognifyapp.data.sources.remote.BaseResponse
import com.cognifyteam.cognifyapp.data.sources.remote.EnrolledCoursesData
import com.cognifyteam.cognifyapp.data.sources.remote.services.CourseService

interface RemoteCourseDataSource {
    suspend fun getEnrolledCourses(firebaseId: String): BaseResponse<EnrolledCoursesData>
}

class RemoteCourseDataSourceImpl(
    private val courseService: CourseService
) : RemoteCourseDataSource {

    override suspend fun getEnrolledCourses(firebaseId: String): BaseResponse<EnrolledCoursesData> {
        return courseService.getEnrolledCourses(firebaseId)
    }
}