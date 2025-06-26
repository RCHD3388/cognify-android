package com.cognifyteam.cognifyapp.data.repositories

import com.cognifyteam.cognifyapp.data.models.Course
import com.cognifyteam.cognifyapp.data.models.UserCourseCrossRef
import com.cognifyteam.cognifyapp.data.sources.local.datasources.LocalCourseDataSource
import com.cognifyteam.cognifyapp.data.sources.remote.course.RemoteCourseDataSource

interface CourseRepository {
    suspend fun getEnrolledCourses(firebaseId: String): Result<List<Course>>
//    suspend fun addCourse(course: Course): Result<Unit>
}

class CourseRepositoryImpl(
    private val localDataSource: LocalCourseDataSource,
    private val remoteDataSource: RemoteCourseDataSource
) : CourseRepository {
    override suspend fun getEnrolledCourses(firebaseId: String): Result<List<Course>> {
        try {
            val response = remoteDataSource.getEnrolledCourses(firebaseId)
            val courseJsons = response.data.courses
            val courses = courseJsons.map { Course.fromJson(it) }

            if (courses.isNotEmpty()) {
                localDataSource.upsertCourses(courses.map { it.toEntity() })
                val crossRefs = courses.map { UserCourseCrossRef(firebaseId, it.courseId) }
                localDataSource.insertUserCourseCrossRefs(crossRefs)
            }
            return Result.success(courses)
        } catch (e: Exception) {
            return try {
                val userWithCourses = localDataSource.getUserWithCourses(firebaseId)
                val cachedCourses = userWithCourses?.courses?.map { Course.fromEntity(it) }
                if (!cachedCourses.isNullOrEmpty()) Result.success(cachedCourses)
                else Result.failure(Exception("No courses found"))
            } catch (cacheError: Exception) { Result.failure(cacheError) }
        }
    }
//    override suspend fun addCourse(course: Course): Result<Unit> {
//        try {
//            val response = remoteDataSource.addCourse(course)
//
//            if (response.isSuccessful) {
//                localDataSource.upsertCourses(listOf(course.toEntity()))
//                return Result.success(Unit)
//            } else {
//                return Result.failure(Exception("Failed to add course"))
//            }
//        } catch (e: Exception) {
//            return Result.failure(e)
//        }
//    }
//    }
}