package com.cognifyteam.cognifyapp.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.parcelize.Parcelize
import java.lang.reflect.Type
import java.util.UUID

// --------------------------
// --- LEARNING PATH STEP ---
// --------------------------

@JsonClass(generateAdapter = true)
data class LearningPathStepJson(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val estimatedTime: String,
    val smartId: Int
)

@Entity(tableName = "smart_steps")
data class LearningPathStepEntity (
    val stepNumber: Int,
    val title: String,
    val description: String,
    val estimatedTime: String,
    val smartId: Int,

    @PrimaryKey(autoGenerate = true)
    val id : Long = 0,
)

@Parcelize
data class LearningPathStep (
    val stepNumber: Int,
    val title: String,
    val description: String,
    val estimatedTime: String,
    val smartId: Int,
): Parcelable{
    companion object {
        fun fromEntity(entity: LearningPathStepEntity): LearningPathStep {
            return LearningPathStep(
                stepNumber = entity.stepNumber,
                title = entity.title,
                description = entity.description,
                estimatedTime = entity.estimatedTime,
                smartId = entity.smartId
            )
        }
        fun fromJson(json: LearningPathStepJson): LearningPathStep {
            return LearningPathStep(
                stepNumber = json.stepNumber,
                title = json.title,
                description = json.description,
                estimatedTime = json.estimatedTime,
                smartId = json.smartId
            )
        }
    }
    fun toJson(): LearningPathStepJson {
        return LearningPathStepJson(
            stepNumber = stepNumber,
            title = title,
            description = description,
            estimatedTime = estimatedTime,
            smartId = smartId
        )
    }
    fun toEntity(): LearningPathStepEntity {
        return LearningPathStepEntity(
            stepNumber = stepNumber,
            title = title,
            description = description,
            estimatedTime = estimatedTime,
            smartId = smartId
        )
    }
}

// -------------------------------
// --- GENERATED LEARNING PATH ---
// -------------------------------

@JsonClass(generateAdapter = true)
data class GeneratedLearningPathJson(
    val paths: List<LearningPathStepJson>,
    val tags: List<String>,
    val level: String,
    val mainDescription: String
)

@Parcelize
data class GeneratedLearningPath(
    val paths: List<LearningPathStep>,
    val tags: List<String>,
    val level: String,
    val mainDescription: String
) : Parcelable {
    companion object {
        fun fromJson(json: GeneratedLearningPathJson): GeneratedLearningPath {
            return GeneratedLearningPath(
                paths = json.paths.map { LearningPathStep.fromJson(it) },
                tags = json.tags,
                level =  json.level,
                mainDescription = json.mainDescription
            )
        }
    }

    fun toJson(): GeneratedLearningPathJson {
        return GeneratedLearningPathJson(
            paths = paths.map { it.toJson() },
            tags = tags,
            level = level,
            mainDescription = mainDescription
        )
    }
}

// -------------------------------
// --- GENERATED LEARNING PATH ---
// -------------------------------

@JsonClass(generateAdapter = true)
data class GenerateLearningPathPayloadJson(
    val topic: String,
    val level: String,
    val additional_prompt: String
)

// ----------------------------------
// --- SAVE LEARNING PATH PAYLOAD ---
// ----------------------------------

@JsonClass(generateAdapter = true)
data class SaveLearningPathPayload(
    val title: String,
    val description: String,
    val author: String,
    val level: String,
    val tags: List<String>,
    val steps: List<LearningPathStep>
)

// ----------------------------------
// --- COMPLETE LEARNING PATH  ------
// ----------------------------------

@Entity(tableName = "smarts")
data class LearningPathEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val title: String,
    val description: String,
    val author_id: String,
    val author_name: String,
    val author_email: String,
    val createdAt: String,
    val level: String,
    val tags: String
)

@JsonClass(generateAdapter = true)
@Parcelize()
data class LearningPath(
    val id: Int,
    val title: String,
    val description: String,
    val author_id: String,
    val author_name: String,
    val author_email: String,
    val createdAt: String,
    val level: String,
    val tags: List<String>,
    val likes: List<SmartLike>,
    val comments: List<SmartComment>,
    val steps: List<LearningPathStep>
): Parcelable {
    companion object {
        fun fromEntity(entity: LearningPathEntity, likes: List<SmartLike>, comments: List<SmartComment>, steps: List<LearningPathStepEntity>): LearningPath {
            return LearningPath(
                id = entity.id,
                title = entity.title,
                description = entity.description,
                author_id = entity.author_id,
                author_name = entity.author_name,
                author_email = entity.author_email,
                createdAt = entity.createdAt,
                level = entity.level,
                tags = Converters.fromString(entity.tags),
                likes = likes,
                comments = comments,
                steps = steps.map { LearningPathStep.fromEntity(it) }
            )
        }
    }
    fun getAuthorInitial(): String {
        val words = this.author_name.trim().split(' ').filter { it.isNotBlank() }
        return when {
            words.isEmpty() -> ""
            words.size == 1 -> {
                words[0].first().toString().uppercase()
            }

            else -> {
                val firstInitial = words[0].first()
                val secondInitial = words[1].first()
                "$firstInitial$secondInitial".uppercase()
            }
        }
    }

    fun toEntity(): LearningPathEntity {
        return LearningPathEntity(
            id = id,
            title = title,
            description = description,
            author_id = author_id,
            author_name = author_name,
            author_email = author_email,
            createdAt = createdAt,
            level = level,
            tags = Converters.fromList(tags)
        )
    }
    fun toLikesEntity(): List<SmartLike> {
        return this.likes.map { like ->
            SmartLike(
                userId = like.userId,
                smartId = like.smartId,
            )
        }
    }
    fun toCommentsEntity(): List<SmartComment> {
        return this.comments.map { comment ->
            SmartComment(
                userId = comment.userId,
                smartId = comment.smartId,
                content = comment.content,
                createdAt = comment.createdAt
            )
        }
    }
    fun toStepsEntity(): List<LearningPathStepEntity> {
        return this.steps.map { step ->
            LearningPathStepEntity(
                stepNumber = step.stepNumber,
                title = step.title,
                description = step.description,
                estimatedTime = step.estimatedTime,
                smartId = step.smartId
            )
        }
    }
}

object Converters {
    @TypeConverter
    fun fromString(value: String?): List<String> {
        if (value.isNullOrBlank()) {
            return emptyList()
        }
        return value.split(',').map { it.trim() }
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return list.joinToString(separator = ",")
    }
}

// Model-model lain tetap sama
@JsonClass(generateAdapter = true)
@Entity(tableName = "smart_likes")
@Parcelize()
data class SmartLike(
    val userId: String,
    val smartId: Int,

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
): Parcelable

@JsonClass(generateAdapter = true)
@Entity(tableName = "smart_comments")
@Parcelize()
data class SmartComment(
    val userId: String,
    val smartId: Int,
    val content: String,
    val createdAt: String,

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
): Parcelable

@JsonClass(generateAdapter = true)
@Parcelize()
data class LikedRes(
    val liked: Boolean,
    val message: String
): Parcelable

@JsonClass(generateAdapter = true)
@Parcelize()
data class LikedBody(
    val userId: String
): Parcelable


