package com.cognifyteam.cognifyapp.data.repositories

import android.content.Context
import android.util.Log
import android.webkit.MimeTypeMap
import com.cognifyteam.cognifyapp.data.models.Course
import com.cognifyteam.cognifyapp.data.models.CourseEntity
import com.cognifyteam.cognifyapp.data.models.CourseJson
import com.cognifyteam.cognifyapp.data.models.CreateMultipleSectionsRequest
import com.cognifyteam.cognifyapp.data.models.Material
import com.cognifyteam.cognifyapp.data.models.MaterialEntity
import com.cognifyteam.cognifyapp.data.models.MaterialJson
import com.cognifyteam.cognifyapp.data.models.Section
import com.cognifyteam.cognifyapp.data.models.SectionRequestBody
import com.cognifyteam.cognifyapp.data.models.UserCourseCrossRef
import com.cognifyteam.cognifyapp.data.sources.local.datasources.LocalCourseDataSource
import com.cognifyteam.cognifyapp.data.sources.remote.CreateCourseRequest
import com.cognifyteam.cognifyapp.data.sources.remote.CreatePaymentRequest
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
    suspend fun getCourses(sortBy: String): Result<List<Course>>
    suspend fun getAllCourses(query: String? = null): Result<List<Course>>
    suspend fun createPayment(courseId: String, createPaymentRequest: CreatePaymentRequest): Result<String>
    suspend fun getSectionsByCourseId(courseId: String): Result<List<Section>>
    suspend fun getMaterialsBySectionId(sectionId: String): Result<List<MaterialJson>>
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
        try {
            // 1. Coba ambil dari remote
            val response = remoteDataSource.getCourseById(courseId)
            val courseJson = response.data.data
            val course = Course.fromJson(courseJson)

            // 2. Jika berhasil, simpan/update ke local
            localDataSource.createCourse(course.toEntity()) // createCourse bisa berfungsi sebagai upsert

            return Result.success(course)
        } catch (e: Exception) {
            // 3. Jika remote gagal, coba ambil dari local
            return try {
                val cachedCourseEntity = localDataSource.getCourseById(courseId)
                if (cachedCourseEntity != null) {
                    Result.success(Course.fromEntity(cachedCourseEntity))
                } else {
                    Log.e("CourseRepository", "Failed to fetch from remote and no cache found for course ID $courseId", e)
                    Result.failure(Exception("Course not found online or offline."))
                }
            } catch (cacheError: Exception) {
                Result.failure(cacheError)
            }
        }
    }

    override suspend fun getEnrolledCourses(firebaseId: String, query: String?): Result<List<Course>> {
        try {
            val response = remoteDataSource.getEnrolledCourses(firebaseId, query)
            val courseJsons = response.data.courses
            val courses = courseJsons.map { Course.fromJson(it) }
            Log.e("isi",courses.toString())
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
            val sectionEntities = createdSections.data.map { it.toEntity() }
            localDataSource.insertSections(sectionEntities)
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
                        val materialEntities = materialResponse.data.data.map { it.toEntity() }
                        localDataSource.insertMaterials(materialEntities)
                    }
                }
            }

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
                Log.d("asd", "berhasil masuk")
                val userWithCourses = localDataSource.getUserWithCourses(firebaseId)
                Log.d("asd", "${userWithCourses}")
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

    override suspend fun getCourses(sortBy: String): Result<List<Course>> {
        return try {
            // response sekarang bertipe BaseResponse<CourseListData>
            val response = remoteDataSource.getCourses(sortBy)

            // "Buka" lapisan: response.data.courses
            // Ini akan mengambil array dari JSON Anda
            val courseJsons = response.data.courses

            // Mapping ke Domain Model tidak berubah
            val courses = courseJsons.map { Course.fromJson(it) }
            if (courses.isNotEmpty()) {
                localDataSource.upsertCourses(courses.map { it.toEntity() })
            }
            Result.success(courses)
        } catch (e: Exception) {
            return Result.runCatching {
                var cachedEntities = localDataSource.getAllCourses()
                // ... (logika filter manual jika ada query) ...
                cachedEntities.map { Course.fromEntity(it) }
            }
        }
    }
    override suspend fun createPayment(courseId: String, createPaymentRequest: CreatePaymentRequest): Result<String> {
        return try {
            val response = remoteDataSource.createPayment(courseId, createPaymentRequest)
            // Ambil token dari respons yang berhasil
            Result.success(response.data.token)
        } catch (e: Exception) {
            Log.e("CourseRepository", "Failed to create payment token", e)
            Result.failure(e)
        }
    }
    override suspend fun getSectionsByCourseId(courseId: String): Result<List<Section>> {
        try {
            // 1. Coba ambil dari remote
            val response = remoteDataSource.getSectionsByCourseId(courseId)
            val sections = response.data.data

            // 2. Jika berhasil dan tidak kosong, simpan/update ke local
            if (sections.isNotEmpty()) {
                // Kita asumsikan Section punya fungsi .toEntity()
                // dan Anda perlu menambahkan courseId secara manual jika tidak ada di respons
                val sectionEntities = sections.map { it.toEntity() }
                val filteredSectionEntities = sectionEntities.filter { it.courseId == courseId }
                localDataSource.insertSections(filteredSectionEntities)
            }
            return Result.success(sections)
        } catch (e: Exception) {
            // 3. Jika remote gagal, coba ambil dari local
            return try {
                val cachedSections = localDataSource.getSectionsForCourse(courseId)
                if (cachedSections.isNotEmpty()) {
                    Result.success(cachedSections.map { Section.fromEntity(it) })
                } else {
                    Log.e("CourseRepository", "Failed to fetch sections from remote and no cache for course ID $courseId", e)
                    Result.failure(Exception("Sections not found."))
                }
            } catch (cacheError: Exception) {
                Result.failure(cacheError)
            }
        }
    }

    override suspend fun getMaterialsBySectionId(sectionId: String): Result<List<Material>> {
        try {
            // 1. Coba ambil dari remote
            val response = remoteDataSource.getMaterialsBySectionId(sectionId)
            val materials = response.data.data

            // 2. Jika berhasil, simpan/update ke local
            if (materials.isNotEmpty()) {
                val materialEntities = materials.map { it.toEntity() }
                localDataSource.insertMaterials(materialEntities)
            }

            return Result.success(materials)
        } catch (e: Exception) {
            // 3. Jika remote gagal, coba ambil dari local
            return try {

                val cachedMaterials = localDataSource.getMaterialsForSection(sectionId)

                if (cachedMaterials.isNotEmpty()) {
                    Result.success(cachedMaterials.map { Material.fromEntity(it) })
                }
//                    Log.e("CourseRepository", "Failed to fetch materials from remote and no cache for section ID $sectionId", e)
//                    Result.failure(Exception("Materials not found."))
//                }
                Result.failure(Exception("Materials not found."))
            } catch (cacheError: Exception) {
                Result.failure(cacheError)
            }
        }
    }
    override suspend fun getCourses(sortBy: String): Result<List<Course>> {
        return try {
            // response sekarang bertipe BaseResponse<CourseListData>
            val response = remoteDataSource.getCourses(sortBy)

            // "Buka" lapisan: response.data.courses
            // Ini akan mengambil array dari JSON Anda
            val courseJsons = response.data.courses

            // Mapping ke Domain Model tidak berubah
            val courses = courseJsons.map { Course.fromJson(it) }
            Result.success(courses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllCourses(query: String?): Result<List<Course>> {
        try {
            // 1. Panggil remote data source dengan query
            val response = remoteDataSource.getAllCourses(query)

            // Backend Anda untuk '/all' tidak memiliki nesting data di dalam data
            val courseJsons = response.data.courses

            // 2. Mapping ke Domain Model
            val courses = courseJsons.map { Course.fromJson(it) }

            // 3. HANYA update cache jika kita TIDAK sedang mencari.
            // Ini mencegah hasil pencarian yang tidak lengkap menimpa cache utama.
            if (query.isNullOrBlank()) {
                localDataSource.upsertCourses(courses.map { it.toEntity() })
            }

            return Result.success(courses)

        } catch (e: Exception) {
            // 4. JIKA network gagal, fallback ke cache.
            // Pencarian di cache dilakukan secara manual.
            return Result.runCatching {
                var cachedEntities = localDataSource.getAllCourses()

                // Jika ada query, filter hasil dari cache
                if (!query.isNullOrBlank()) {
                    cachedEntities = cachedEntities.filter {
                        it.name.contains(query, ignoreCase = true)
                    }
                }

                cachedEntities.map { Course.fromEntity(it) }
            }
        }
    }

    override suspend fun getMaterialsBySectionId(sectionId: String): Result<List<MaterialJson>> {
        return try {
            val response = remoteDataSource.getMaterialsBySectionId(sectionId)
            val materials = response.data.data
            Result.success(materials)
        } catch (e: Exception) {
            Log.e("CourseRepository", "Failed to fetch materials by section ID", e)
            Result.failure(e)
        }
    }
}