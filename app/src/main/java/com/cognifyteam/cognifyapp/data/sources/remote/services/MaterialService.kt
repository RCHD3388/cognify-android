package com.cognifyteam.cognifyapp.data.sources.remote.services

import com.cognifyteam.cognifyapp.data.models.MaterialJson
import com.cognifyteam.cognifyapp.data.sources.remote.ApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface MaterialService {

    /**
     * Mengunggah beberapa materi untuk satu section.
     * Menggunakan multipart request untuk mengirim metadata (JSON string) dan file.
     * @param sectionId ID dari section.
     * @param materialsJson RequestBody berisi string JSON dari array metadata material.
     * @param files List dari file (dokumen/video) yang akan diunggah.
     */
    @Multipart
    @POST("material/materials/{sectionId}")
    suspend fun createMultipleMaterials(
        @Path("sectionId") sectionId: String,
        @Part("materials") materialsJson: RequestBody,
        @Part files: List<MultipartBody.Part>
    ): ApiResponse<List<MaterialJson>>

    @GET("material/materials/{sectionId}")
    suspend fun getMaterialsBySectionId(@Path("sectionId") sectionId: String): ApiResponse<List<MaterialJson>>
}