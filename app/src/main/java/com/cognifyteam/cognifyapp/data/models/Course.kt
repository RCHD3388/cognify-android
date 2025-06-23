package com.cognifyteam.cognifyapp.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
data class CourseJson(
    @Json(name = "course_id")
    val courseId: String,

    @Json(name = "course_name")
    val courseName: String,

    @Json(name = "course_description")
    val courseDescription: String,

    @Json(name = "course_rating")
    val courseRating: String
)

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey
    val courseId: String,
    val name: String,
    val description: String,
    val rating: String
)

@Parcelize
data class Course(
    val courseId: String,
    val name: String,
    val description: String,
    val rating: String
) : Parcelable {
    companion object {
        fun fromJson(json: CourseJson): Course {
            return Course(
                courseId = json.courseId,
                name = json.courseName,
                description = json.courseDescription,
                rating = json.courseRating
            )
        }

        fun fromEntity(entity: CourseEntity): Course {
            return Course(
                courseId = entity.courseId,
                name = entity.name,
                description = entity.description,
                rating = entity.rating
            )
        }
    }

    fun toEntity(): CourseEntity {
        return CourseEntity(
            courseId = this.courseId,
            name = this.name,
            description = this.description,
            rating = this.rating
        )
    }
}