package com.cognifyteam.cognifyapp.data.repositories

import android.util.Log
import com.cognifyteam.cognifyapp.data.models.Course
import com.cognifyteam.cognifyapp.data.models.CourseJson
import com.cognifyteam.cognifyapp.data.models.UserCourseCrossRef
import com.cognifyteam.cognifyapp.data.sources.local.datasources.LocalCourseDataSource
import com.cognifyteam.cognifyapp.data.sources.remote.course.RemoteCourseDataSource
import java.io.File
import com.squareup.moshi.Moshi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

interface CourseRepository {
    suspend fun getEnrolledCourses(firebaseId: String, query: String? = null): Result<List<Course>>
    suspend fun createCourse(courseData: CourseJson, thumbnailFile: File): Result<Course>
}
fun String.toPlainTextRequestBody(): RequestBody {
    return this.toRequestBody("text/plain".toMediaTypeOrNull())
}
class CourseRepositoryImpl(
    private val localDataSource: LocalCourseDataSource,
    private val remoteDataSource: RemoteCourseDataSource,
    private val moshi: Moshi
) : CourseRepository {
    override suspend fun getEnrolledCourses(firebaseId: String, query: String?): Result<List<Course>> {
        try {
            val response = remoteDataSource.getEnrolledCourses(firebaseId, query)
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
                else {
                    Log.d("CourseRepositoryImpl", "No cached courses found for user: $e")
                    Result.failure(Exception("No courses found"))
                }
            } catch (cacheError: Exception) { Result.failure(cacheError) }
        }
    }

    override suspend fun createCourse(courseData: CourseJson, thumbnailFile: File): Result<Course> {
        // 1. Siapkan file gambar (sama seperti sebelumnya)
        val requestImageFile = thumbnailFile.asRequestBody("image/*".toMediaTypeOrNull())
        val imageMultipart = MultipartBody.Part.createFormData(
            "thumbnail",
            thumbnailFile.name,
            requestImageFile
        )

        // 2. Siapkan semua data teks sebagai RequestBody terpisah dalam sebuah Map
        val courseDataMap = mapOf(
            "course_name" to courseData.course_name.toPlainTextRequestBody(),
            "course_description" to courseData.course_description.toPlainTextRequestBody(),
            "course_owner" to courseData.course_owner.toPlainTextRequestBody(),
            "course_price" to courseData.course_price.toString().toPlainTextRequestBody(),
            "category_id" to courseData.category_id.toPlainTextRequestBody()
        )

        // 3. Panggil remote data source dengan format yang baru
        return remoteDataSource.createCourse(imageMultipart, courseDataMap)
    }
}