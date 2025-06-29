package com.cognifyteam.cognifyapp.data.repositories

import android.util.Log
import android.webkit.MimeTypeMap
import com.cognifyteam.cognifyapp.data.models.Course
import com.cognifyteam.cognifyapp.data.models.CourseEntity
import com.cognifyteam.cognifyapp.data.models.CourseJson
import com.cognifyteam.cognifyapp.data.models.UserCourseCrossRef
import com.cognifyteam.cognifyapp.data.sources.local.datasources.LocalCourseDataSource
import com.cognifyteam.cognifyapp.data.sources.remote.CreateCourseRequest
import com.cognifyteam.cognifyapp.data.sources.remote.course.RemoteCourseDataSource
import java.io.File
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.math.log

interface CourseRepository {
    suspend fun createCourse(course_name: String, course_description: String, course_owner: String, course_price: Int, category_id: String, thumbnail: File, course_owner_name: String): Result<Course>
    suspend fun getUserCreatedCourses(firebaseId: String): Result<List<Course>>
    suspend fun getEnrolledCourses(firebaseId: String, query: String? = null): Result<List<Course>>
    suspend fun getCourseById(courseId: String): Result<Course>
}
fun String.toPlainTextRequestBody(): RequestBody {
    return this.toRequestBody("text/plain".toMediaTypeOrNull())
}

class CourseRepositoryImpl(
    private val localDataSource: LocalCourseDataSource,
    private val remoteDataSource: RemoteCourseDataSource,
) : CourseRepository {
    override suspend fun getCourseById(courseId: String): Result<Course> {
        return try {
            val response = remoteDataSource.getCourseById(courseId)
            val courseDataWrapper = response.data
            val courseJson = courseDataWrapper.data
            val course = Course.fromJson(courseJson)

            Result.success(course)
        } catch (e: Exception) {
            Log.e("CourseRepository", "Error fetching course details by ID", e)
            Result.failure(e)
        }
    }

    override suspend fun getEnrolledCourses(firebaseId: String, query: String?): Result<List<Course>> {
        try {
            val response = remoteDataSource.getEnrolledCourses(firebaseId, query)
            val courseJsons = response.data.courses
            val courses = courseJsons.map { Course.fromJson(it) }
            Log.e("isi",courses.toString())
//            if (courses.isNotEmpty()) {
//                localDataSource.upsertCourses(courses.map { it.toEntity() })
//                val crossRefs = courses.map { UserCourseCrossRef(firebaseId, it.courseId) }
//                localDataSource.insertUserCourseCrossRefs(crossRefs)
//            }
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

    override suspend fun createCourse(
        course_name: String,
        course_description: String,
        course_owner: String,
        course_price: Int,
        category_id: String,
        thumbnail: File,
        course_owner_name: String
    ): Result<Course> {

        // 1. Buat MultipartBody.Part untuk file (ini sudah benar)
        val extension = MimeTypeMap.getFileExtensionFromUrl(thumbnail.path)

        // 2. Dapatkan MIME type dari ekstensi tersebut
        // Jika tidak terdeteksi, gunakan "image/*" sebagai default
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "image/*"

        // 3. Gunakan MIME type yang dinamis saat membuat RequestBody
        val requestImageFile = thumbnail.asRequestBody(mimeType.toMediaTypeOrNull())

        // 4. Lanjutkan membuat MultipartBody.Part (ini tidak berubah)
        val imageMultipart = MultipartBody.Part.createFormData(
            "thumbnail",
            thumbnail.name,
            requestImageFile
        )

        // 2. Buat RequestBody untuk setiap field data.
        // Hindari membuat object `CreateCourseRequest` di sini.
        val courseNameBody = course_name.toRequestBody("text/plain".toMediaTypeOrNull())
        val descriptionBody = course_description.toRequestBody("text/plain".toMediaTypeOrNull())
        val ownerBody = course_owner.toRequestBody("text/plain".toMediaTypeOrNull())
        val priceBody = course_price.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val categoryIdBody = category_id.toRequestBody("text/plain".toMediaTypeOrNull())
        val course_owner_name = course_owner_name.toRequestBody("text/plain".toMediaTypeOrNull())

        // 3. Panggil DataSource dengan parameter yang sudah benar dan lengkap
        val response = remoteDataSource.createCourse(
            thumbnail = imageMultipart,
            course_name = courseNameBody,
            course_description = descriptionBody,
            course_owner = ownerBody,
            course_price = priceBody,
            category_id = categoryIdBody,
            course_owner_name = course_owner_name
        )

        response.data?.let {
            val newCourse = Course.fromJson(it.data)
            localDataSource.createCourse(newCourse.toEntity())
            return Result.success(newCourse)
        }

        return Result.failure(Exception("Failed to create course or response was empty"))
    }

    override suspend fun getUserCreatedCourses(firebaseId: String): Result<List<Course>> {
        try {
            val response = remoteDataSource.getUserCreatedCourses(firebaseId)
            val courseJsons = response.data.data
            val courses = courseJsons.map { Course.fromJson(it) }
            Log.d("Courses", "ini Courses dari repo: $courses")
            return Result.success(courses)
        }catch (Exception: Exception){
            return try {
                val userWithCourses = localDataSource.getUserWithCourses(firebaseId)
                val cachedCourses = userWithCourses?.courses?.map { Course.fromEntity(it) }
                if (!cachedCourses.isNullOrEmpty()) Result.success(cachedCourses)
                else Result.failure(Exception("No courses found"))
            } catch (cacheError: Exception) { Result.failure(cacheError) }

        }
    }
}