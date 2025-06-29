// Berkas: data/sources/remote/course/RemoteCourseDataSource.kt

package com.cognifyteam.cognifyapp.data.sources.remote.course

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import com.cognifyteam.cognifyapp.data.models.CourseJson
import com.cognifyteam.cognifyapp.data.models.UserCoursesDataWrapper
import com.cognifyteam.cognifyapp.data.models.CreateMultipleSectionsRequest
import com.cognifyteam.cognifyapp.data.models.DiscussionJson
import com.cognifyteam.cognifyapp.data.models.Material
import com.cognifyteam.cognifyapp.data.models.MaterialJson
import com.cognifyteam.cognifyapp.data.models.MaterialRequestBody
import com.cognifyteam.cognifyapp.data.models.Section
import com.cognifyteam.cognifyapp.data.sources.remote.ApiResponse
import com.cognifyteam.cognifyapp.data.sources.remote.BaseResponse
import com.cognifyteam.cognifyapp.data.sources.remote.CourseDataWrapper
import com.cognifyteam.cognifyapp.data.sources.remote.CreatePaymentRequest
import com.cognifyteam.cognifyapp.data.sources.remote.EnrolledCoursesData
import com.cognifyteam.cognifyapp.data.sources.remote.services.CourseService
import com.cognifyteam.cognifyapp.data.sources.remote.services.MaterialService
import com.cognifyteam.cognifyapp.data.sources.remote.services.PaymentTokenResponse
import com.cognifyteam.cognifyapp.data.sources.remote.services.SectionService
import com.cognifyteam.cognifyapp.ui.course.addcourse.MaterialState
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

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
        category_id: RequestBody,
        course_owner_name: RequestBody
    ): BaseResponse<CourseDataWrapper>

    suspend fun getUserCreatedCourses(firebaseId: String): BaseResponse<UserCoursesDataWrapper>

    suspend fun createSection(course_id: String,createMultipleSectionsRequest: CreateMultipleSectionsRequest) : ApiResponse<List<Section>>

    suspend fun getSectionsByCourse(course_id: String): ApiResponse<List<Section>>
    suspend fun createMaterialsForSection(
        sectionId: String,
        materials: List<MaterialState>,
        context: Context
    ): ApiResponse<List<Material>>

    suspend fun createPayment(courseId: String, createPaymentRequest: CreatePaymentRequest): BaseResponse<PaymentTokenResponse>
    suspend fun getSectionsByCourseId(courseId: String): ApiResponse<List<Section>>

    suspend fun getMaterialsBySectionId(sectionId: String): ApiResponse<List<Material>>
}

class RemoteCourseDataSourceImpl(
    private val courseService: CourseService,
    private val sectionService: SectionService,
    private val materialService: MaterialService, // <-- Dependency baru
    private val moshi: Moshi
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
        categoryId: RequestBody,
        course_owner_name: RequestBody
    ): BaseResponse<CourseDataWrapper> {

        return courseService.createCourse(
            thumbnail = thumbnail,
            course_name = courseName,
            course_description = courseDescription,
            course_owner = courseOwner,
            course_price = coursePrice,
            category_id = categoryId,
            course_owner_name = course_owner_name
        )
    }

    override suspend fun getUserCreatedCourses(firebaseId: String): BaseResponse<UserCoursesDataWrapper> {
        return courseService.getUserCreatedCourses(firebaseId)
    }

    override suspend fun createSection(course_id: String, createMultipleSectionsRequest: CreateMultipleSectionsRequest): ApiResponse<List<Section>> {
        return sectionService.createMultipleSections(course_id, createMultipleSectionsRequest)
    }

    override suspend fun getSectionsByCourse(course_id: String): ApiResponse<List<Section>> {
        return sectionService.getSectionsByCourse(course_id)
    }
    override suspend fun createMaterialsForSection(
        sectionId: String,
        materials: List<MaterialState>,
        context: Context
    ): ApiResponse<List<Material>> {
        // 1. Buat metadata JSON
        val materialRequestBodies = materials.mapIndexed { index, materialState ->
            MaterialRequestBody(
                title = materialState.title,
                description = materialState.description,
                material_type = materialState.material_type,
                position = index + 1
            )
        }
        val listType = Types.newParameterizedType(List::class.java, MaterialRequestBody::class.java)
        val jsonAdapter = moshi.adapter<List<MaterialRequestBody>>(listType)
        val materialsJsonString = jsonAdapter.toJson(materialRequestBodies)
        val materialsJsonRequestBody = materialsJsonString.toRequestBody("application/json".toMediaTypeOrNull())

        // 2. Siapkan file-file untuk diunggah
        val fileParts = materials.mapNotNull { materialState ->
            materialState.fileUri?.let { uri ->
                uriToFile(context, uri)?.let { file ->
                    val mimeType = getMimeTypeFromUri(context, uri)
                    val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
                    // Backend mengharapkan nama field "files"
                    MultipartBody.Part.createFormData("files", file.name, requestFile)
                }
            }
        }

        // 3. Panggil API service
        return materialService.createMultipleMaterials(
            sectionId = sectionId,
            materialsJson = materialsJsonRequestBody,
            files = fileParts
        )
    }
    private fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileName = getFileNameFromUri(context, uri)
            val file = File(context.cacheDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getFileNameFromUri(context: Context, uri: Uri): String {
        var fileName: String? = null
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex)
                }
            }
        }
        return fileName ?: "temp_file_${System.currentTimeMillis()}"
    }

    private fun getMimeTypeFromUri(context: Context, uri: Uri): String {
        return context.contentResolver.getType(uri)
            ?: MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            ) ?: "application/octet-stream"
    }

    override suspend fun createPayment(courseId: String, createPaymentRequest: CreatePaymentRequest): BaseResponse<PaymentTokenResponse> {
        return courseService.createPayment(courseId, createPaymentRequest)
    }

    override suspend fun getSectionsByCourseId(courseId: String): ApiResponse<List<Section>> {
        return sectionService.getSectionsByCourse(courseId)
    }

    override suspend fun getMaterialsBySectionId(sectionId: String): ApiResponse<List<Material>> {
        return materialService.getMaterialsBySectionId(sectionId)
    }
}