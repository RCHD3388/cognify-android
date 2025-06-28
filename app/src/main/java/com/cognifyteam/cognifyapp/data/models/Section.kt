// File: data/models/Section.kt

package com.cognifyteam.cognifyapp.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

// Model untuk komunikasi API (GET response)
@JsonClass(generateAdapter = true)
data class SectionJson(
    @Json(name = "id")
    val id: String,
    @Json(name = "title")
    val title: String,
    @Json(name = "position")
    val position: Int,
    @Json(name = "course_id")
    val courseId: String
)

// Model untuk request body (POST)
@JsonClass(generateAdapter = true)
data class SectionRequestBody(
    @Json(name = "title")
    val title: String,
    @Json(name = "position")
    val position: Int
)

@JsonClass(generateAdapter = true)
data class CreateMultipleSectionsRequest(
    @Json(name = "sections")
    val sections: List<SectionRequestBody>
)


data class SectionEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val position: Int,
    val courseId: String // Kolom untuk foreign key
)

// Model domain yang digunakan di UI (ViewModel, Composable)
@Parcelize
data class Section(
    val id: String,
    val title: String,
    val position: Int,
    val courseId: String
) : Parcelable {
    companion object {
        // Mapper dari model JSON ke model Domain
        fun fromJson(json: SectionJson): Section {
            return Section(
                id = json.id,
                title = json.title,
                position = json.position,
                courseId = json.courseId
            )
        }

        // Mapper dari model Entity ke model Domain
        fun fromEntity(entity: SectionEntity): Section {
            return Section(
                id = entity.id,
                title = entity.title,
                position = entity.position,
                courseId = entity.courseId
            )
        }
    }

    // Mapper dari model Domain ke model Entity
    fun toEntity(): SectionEntity {
        return SectionEntity(
            id = this.id,
            title = this.title,
            position = this.position,
            courseId = this.courseId
        )
    }
}