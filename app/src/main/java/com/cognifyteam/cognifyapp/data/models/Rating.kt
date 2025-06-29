package com.cognifyteam.cognifyapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// DTO untuk JSON dari network
@JsonClass(generateAdapter = true)
data class RatingJson(
    @Json(name = "id") val id: Int,
    @Json(name = "rating") val rating: Int,
    @Json(name = "comment") val comment: String?,
    @Json(name = "Author") val author: AuthorJson?
)

// Entity untuk database Room
@Entity(tableName = "ratings")
data class RatingEntity(
    @PrimaryKey val id: Int,
    val rating: Int,
    val comment: String?,
    val userId: String,
    val courseId: String,
    val authorName: String // Denormalisasi untuk tampilan offline yang mudah
)

// Domain Model untuk UI
data class Rating(
    val id: Int,
    val rating: Int,
    val comment: String?,
    val authorName: String
) {
    companion object {
        fun fromJson(json: RatingJson): Rating {
            return Rating(
                id = json.id,
                rating = json.rating,
                comment = json.comment,
                authorName = json.author?.name ?: "Unknown User"
            )
        }
    }
    // ... mapper lain jika perlu ...
}