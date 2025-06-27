package com.cognifyteam.cognifyapp.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cognifyteam.cognifyapp.data.sources.remote.UserSearchData
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.util.Date


@JsonClass(generateAdapter = true)
data class UserJson(
    val firebaseId:String,
    var name:String,
    var email:String,
    var role: String,
    var description: String? = null
)

@Entity(tableName = "users")
data class UserEntity (
    @PrimaryKey(autoGenerate = false)
    val firebaseId: String,
    var name:String,
    var email: String,
    var role: String,
    val isFollowing: Boolean = false,
    var description: String? = null
)

@Parcelize
data class User (
    val firebaseId: String,
    val name: String,
    val email: String,
    var role: String,
    var description: String? = null
): Parcelable{
    companion object {
        fun fromEntity(entity: UserEntity): User {
            return User(
                firebaseId = entity.firebaseId,
                name = entity.name,
                email = entity.email,
                role = entity.role,
                description = entity.description
            )
        }
        fun fromJson(json: UserJson): User {
            return User(
                firebaseId = json.firebaseId,
                name = json.name,
                email = json.email,
                role = json.role,
                description = json.description
            )
        }
    }
    fun toJson(): UserJson {
        return UserJson(
            firebaseId = firebaseId,
            name = name,
            email = email,
            role = role,
            description = this.description
        )
    }
    fun toEntity(): UserEntity {
        return UserEntity(
            firebaseId = firebaseId,
            name = name,
            email = email,
            role = role,
            description = this.description
        )
    }
}

