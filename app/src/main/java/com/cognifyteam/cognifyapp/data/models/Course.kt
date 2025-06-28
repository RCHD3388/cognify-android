package com.cognifyteam.cognifyapp.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
 * PERBAIKAN: Model JSON untuk komunikasi dengan API.
 * Field yang dibuat oleh server (course_id, course_rating, course_thumbnail)
 * dibuat nullable (String?) agar class ini bisa digunakan untuk:
 * 1. MENGIRIM data (saat membuat course, field ini akan null/tidak ada).
 * 2. MENERIMA data (saat mendapat detail course, field ini akan berisi data dari server).
 */
@JsonClass(generateAdapter = true)
data class CourseJson(
    // Dibuat nullable karena tidak ada saat MEMBUAT course baru.
    @Json(name = "course_id")
    val course_id: String?,

    @Json(name = "course_name")
    val course_name: String,

    @Json(name = "course_description")
    val course_description: String,

    @Json(name = "course_owner")
    val course_owner: String,

    @Json(name="course_price")
    val course_price: String,

    @Json(name = "category_id")
    val category_id: String,

    // Dibuat nullable karena diatur oleh server.
    @Json(name = "course_rating")
    val course_rating: String?,

    // Dibuat nullable karena URL-nya diatur oleh server setelah upload.
    @Json(name = "course_thumbnail")
    val course_thumbnail: String?,

    @Json(name = "course_owner_name")
    val course_owner_name: String
)

//wrapper untuk course yang dibuat oleh user
@JsonClass(generateAdapter = true)
data class UserCoursesDataWrapper(
    @Json(name = "message")
    val message: String,

    @Json(name = "data")
    val data: List<CourseJson>
)

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey
    val courseId: String,
    val name: String,
    val description: String,
    val rating: String,
    val thumbnail: String,
    val price: Int,
    val course_owner: String,
    val course_owner_name: String
)

@Parcelize
data class Course(
    val courseId: String,
    val name: String,
    val description: String,
    val rating: String,
    val thumbnail: String,
    val price: String,
    val course_owner: String = "",
    val course_owner_name: String
) : Parcelable {
    companion object {
        fun fromJson(json: CourseJson): Course {
            val fullThumbnailUrl = if (!json.course_thumbnail.isNullOrBlank()) {
                // Hapus garis miring di awal jika ada, untuk menghindari URL ganda seperti http://...//uploads
                val cleanPath = json.course_thumbnail.removePrefix("/")
                "http://10.0.2.2:3000/$cleanPath"
            } else {
                json.course_thumbnail
            }
            return Course(
                // Tambahkan pengecekan null untuk keamanan
                courseId = json.course_id ?: "",
                name = json.course_name,
                description = json.course_description,
                rating = json.course_rating ?: "0.0",
                thumbnail = fullThumbnailUrl ?: "",
                price = json.course_price,
                course_owner = json.course_owner,
                course_owner_name = json.course_owner_name
            )
        }

        fun fromEntity(entity: CourseEntity): Course {
            return Course(
                courseId = entity.courseId,
                name = entity.name,
                description = entity.description,
                rating = entity.rating,
                thumbnail = entity.thumbnail,
                price = entity.price.toString(),
                course_owner = entity.course_owner,
                course_owner_name = entity.course_owner_name
            )
        }
    }

    fun toEntity(): CourseEntity {
        return CourseEntity(
            courseId = this.courseId,
            name = this.name,
            description = this.description,
            rating = this.rating,
            thumbnail = this.thumbnail,
            price = this.price.toInt(),
            course_owner = this.course_owner,
            course_owner_name = this.course_owner_name
        )
    }
}
