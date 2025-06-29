package com.cognifyteam.cognifyapp.data.repositories

import android.content.Context
import android.util.Log
import android.webkit.MimeTypeMap
import com.cognifyteam.cognifyapp.data.models.Course
import com.cognifyteam.cognifyapp.data.models.CourseEntity
import com.cognifyteam.cognifyapp.data.models.CourseJson
import com.cognifyteam.cognifyapp.data.models.CreateMultipleSectionsRequest
import com.cognifyteam.cognifyapp.data.models.SectionRequestBody
import com.cognifyteam.cognifyapp.data.models.UserCourseCrossRef
import com.cognifyteam.cognifyapp.data.sources.local.datasources.LocalCourseDataSource
import com.cognifyteam.cognifyapp.data.sources.remote.CreateCourseRequest
import com.cognifyteam.cognifyapp.data.sources.remote.course.RemoteCourseDataSource
import com.cognifyteam.cognifyapp.ui.course.addcourse.SectionState
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
    suspend fun getUserCreatedCourses(firebaseId: String): Result<List<Course>>
    suspend fun getEnrolledCourses(firebaseId: String, query: String? = null): Result<List<Course>>
    suspend fun getCourseById(courseId: String): Result<Course>
    suspend fun createCourse(
        course_name: String,
        course_description: String,
        course_owner: String,
        course_price: Int,
        category_id: String,
        thumbnail: File,
        sectionsWithMaterials: List<SectionState>,
        course_owner_name: String
    ): Result<Course>
    suspend fun createSection(courseId: String, createMultipleSectionsRequest: CreateMultipleSectionsRequest): Result<Course>
}
fun String.toPlainTextRequestBody(): RequestBody {
    return this.toRequestBody("text/plain".toMediaTypeOrNull())
}

class CourseRepositoryImpl(
    private val localDataSource: LocalCourseDataSource,
    private val remoteDataSource: RemoteCourseDataSource,
    private val context: Context
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
        sectionsWithMaterials: List<SectionState>,
        course_owner_name: String
    ): Result<Course> {
        return try {
            // TAHAP 1: Membuat Course Dasar (Tidak ada perubahan)
            val imageMultipart = thumbnail.toMultipartBodyPart("thumbnail")
            val courseResponse = remoteDataSource.createCourse(
                thumbnail = imageMultipart,
                course_name = course_name.toPlainTextRequestBody(),
                course_description = course_description.toPlainTextRequestBody(),
                course_owner = course_owner.toPlainTextRequestBody(),
                course_price = course_price.toString().toPlainTextRequestBody(),
                category_id = category_id.toPlainTextRequestBody(),
                course_owner_name = course_owner_name.toPlainTextRequestBody()
            )

            val newCourse = courseResponse.data?.data?.let { Course.fromJson(it) }
                ?: return Result.failure(Exception("Failed to create course or response was empty."))

            localDataSource.createCourse(newCourse.toEntity())

            if (sectionsWithMaterials.isEmpty()) {
                return Result.success(newCourse)
            }

            // TAHAP 2: Membuat Sections (Tidak ada perubahan)
            val sectionRequestBodies = sectionsWithMaterials.mapIndexed { index, sectionState ->
                SectionRequestBody(title = sectionState.title, position = index + 1)
            }
            val createSectionsRequest = CreateMultipleSectionsRequest(sectionRequestBodies)
            val sectionResponse = remoteDataSource.createSection(newCourse.courseId, createSectionsRequest)

            // Memastikan `createdSections` tidak null sebelum digunakan
            val createdSections = sectionResponse.data
                ?: return Result.failure(Exception("Course created, but failed to create sections."))

            // ======================================================
            // TAHAP 3: UPLOAD MATERIALS (TANPA .zip())
            // ======================================================
            // Menggunakan perulangan 'for' dengan index sebagai alternatif .zip()
            // Ini adalah cara yang paling aman untuk menghindari error Anda.
            sectionsWithMaterials.forEachIndexed { index, sectionState ->
                // Pastikan index tidak melebihi batas array `createdSections`
                if (index < createdSections.data.size) {
                    val createdSection = createdSections.data[index]

                    if (sectionState.materials.isNotEmpty()) {
                        val materialResponse = remoteDataSource.createMaterialsForSection(
                            sectionId = createdSection.id.toString(),
                            materials = sectionState.materials.toList(),
                            context = context
                        )
                        if (materialResponse.status != "success") {
                            // Keluar dari fungsi dengan failure jika ada error
                            return@createCourse Result.failure(Exception("Failed to upload materials for section: '${sectionState.title}'."))
                        }
                    }
                }
            }
            // ======================================================

            Result.success(newCourse)

        } catch (e: Exception) {
            Log.e("CourseRepository", "An error occurred during course creation process", e)
            Result.failure(e)
        }
    }

    private fun String.toPlainTextRequestBody(): RequestBody = this.toRequestBody("text/plain".toMediaTypeOrNull())

    private fun File.toMultipartBodyPart(partName: String): MultipartBody.Part {
        val requestBody = this.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, this.name, requestBody)
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

    override suspend fun createSection(courseId: String, createMultipleSectionsRequest: CreateMultipleSectionsRequest): Result<Course> {
        val response = remoteDataSource.createSection(courseId, createMultipleSectionsRequest)


        return Result.failure(Exception("Failed to create course or response was empty"))
    }
}