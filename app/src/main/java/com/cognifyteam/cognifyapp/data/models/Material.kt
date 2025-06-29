package com.cognifyteam.cognifyapp.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MaterialRequestBody(
    @Json(name = "title")
    val title: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "material_type")
    val material_type: String,
    @Json(name = "position")
    val position: Int
)

/**
 * Model data untuk menerima respons material dari server.
 */
@JsonClass(generateAdapter = true)
data class MaterialJson(
    @Json(name = "id")
    val id: Int,
    @Json(name = "title")
    val title: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "material_type")
    val material_type: String,
    @Json(name = "position")
    val position: Int,
    @Json(name = "url")
    val url: String?,
    @Json(name = "section_id")
    val section_id: String // Diubah ke String untuk cocok dengan respons "1"
)
