package com.cognifyteam.cognifyapp.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

// 1. --- MODEL UNTUK KOMUNIKASI API ---

// Model untuk me-request pembuatan material (POST body)
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

// Wrapper untuk response saat membuat material
@JsonClass(generateAdapter = true)
data class CreateMaterialsDataWrapper(
    @Json(name = "message")
    val message: String,
    @Json(name = "data")
    val data: List<MaterialJson>
)

// Model untuk menerima data material dari API (GET response)
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
    val section_id: String
)


// 2. --- MODEL UNTUK DATABASE (ROOM) ---

@Entity(tableName = "materials")
data class MaterialEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val description: String,
    val materialType: String,
    val position: Int,
    val url: String?,
    val sectionId: String // Kolom untuk foreign key ke tabel 'sections'
)


// 3. --- MODEL DOMAIN (UNTUK UI & VIEWMODEL) ---

@Parcelize
data class Material(
    val id: Int,
    val title: String,
    val description: String,
    val material_type: String,
    val position: Int,
    val url: String?,
    val section_id: String
) : Parcelable {

    // 4. --- MAPPERS ---

    companion object {
        // Mapper dari model JSON (API) ke model Domain
        fun fromJson(json: MaterialJson): Material {
            return Material(
                id = json.id,
                title = json.title,
                description = json.description,
                material_type = json.material_type,
                position = json.position,
                url = json.url,
                section_id = json.section_id
            )
        }

        // Mapper dari model Entity (Database) ke model Domain
        fun fromEntity(entity: MaterialEntity): Material {
            return Material(
                id = entity.id,
                title = entity.title,
                description = entity.description,
                material_type = entity.materialType,
                position = entity.position,
                url = entity.url,
                section_id = entity.sectionId
            )
        }
    }

    // Mapper dari model Domain ke model Entity (Database)
    fun toEntity(): MaterialEntity {
        return MaterialEntity(
            id = this.id,
            title = this.title,
            description = this.description,
            materialType = this.material_type,
            position = this.position,
            url = this.url,
            sectionId = this.section_id
        )
    }
}