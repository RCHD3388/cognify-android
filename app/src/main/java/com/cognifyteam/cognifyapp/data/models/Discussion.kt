package com.cognifyteam.cognifyapp.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

// DTO untuk Network, merepresentasikan satu post diskusi dari JSON
@JsonClass(generateAdapter = true)
data class DiscussionJson(
    @Json(name = "id")
    val id: Int,

    @Json(name = "content")
    val content: String,

    @Json(name = "createdAt")
    val createdAt: String,

    @Json(name = "Author") // Objek Author yang ter-nesting
    val author: AuthorJson?, // Dibuat nullable untuk keamanan

    @Json(name = "Replies") // Daftar balasan yang ter-nesting
    val replies: List<DiscussionJson>? // Daftar DiscussionJson juga
)

// DTO untuk objek Author yang ter-nesting di dalam DiscussionJson
@JsonClass(generateAdapter = true)
data class AuthorJson(
    @Json(name = "firebaseId")
    val firebaseId: String,

    @Json(name = "name")
    val name: String
)

// Entity untuk Database. Ini akan menyimpan semua post, baik induk maupun balasan.
@Entity(
    tableName = "discussions",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["firebaseId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["courseId"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE
        ),
        // Relasi ke dirinya sendiri untuk balasan
        ForeignKey(
            entity = DiscussionEntity::class,
            parentColumns = ["id"],
            childColumns = ["parentId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DiscussionEntity(
    @PrimaryKey
    val id: Int,
    val content: String,
    val createdAt: String,
    val userId: String, // FK ke UserEntity
    val courseId: String, // FK ke CourseEntity
    val parentId: Int? // FK ke dirinya sendiri, nullable jika post utama
)

// Domain Model untuk UI dan ViewModel.
@Parcelize
data class Discussion(
    val id: Int,
    val content: String,
    val createdAt: String,
    val authorName: String,
    val authorId: String,
    val replies: List<Discussion> // Balasan juga bertipe Discussion
) : Parcelable {
    companion object {
        // Mapper dari JSON (network) ke Domain Model (UI)
        fun fromJson(json: DiscussionJson): Discussion {
            return Discussion(
                id = json.id,
                content = json.content,
                createdAt = json.createdAt,
                authorName = json.author?.name ?: "Unknown",
                authorId = json.author?.firebaseId ?: "",
                // Mapping rekursif untuk balasan
                replies = json.replies?.map { fromJson(it) } ?: emptyList()
            )
        }
    }

    fun toReplyEntity(courseId: String, parentId: Int): DiscussionEntity {
        return DiscussionEntity(
            id = this.id,
            content = this.content,
            createdAt = this.createdAt,
            userId = this.authorId,
            courseId = courseId, // Gunakan courseId dari induk
            parentId = parentId   // Set parentId
        )
    }

    // Mapper dari Domain Model (UI) ke Entity (database)
    fun toEntity(courseId: String): List<DiscussionEntity> {
        val parentEntity = DiscussionEntity(
            id = this.id,
            content = this.content,
            createdAt = this.createdAt,
            userId = this.authorId,
            courseId = courseId,
            parentId = null // Ini adalah post utama
        )
        val replyEntities = this.replies.map { reply ->
            DiscussionEntity(
                id = reply.id,
                content = reply.content,
                createdAt = reply.createdAt,
                userId = reply.authorId,
                courseId = courseId, // Balasan juga milik kursus yang sama
                parentId = this.id // parentId adalah id dari post utama ini
            )
        }
        return listOf(parentEntity) + replyEntities
    }
}