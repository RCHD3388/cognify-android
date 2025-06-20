package com.cognifyteam.cognifyapp.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.util.Date
@JsonClass(generateAdapter = true)
data class UserJson(
    val firebaseId:String,
    var name:String,
    var email:String
)

@Entity(tableName = "users")
data class UserEntity (
    @PrimaryKey(autoGenerate = false)
    val firebaseId: String,
    var name:String,
    var email: String,
    var role: String,
)

@Parcelize
data class User (
    val firebaseId: String,
    val name: String,
    val email: String,
    var role: String,
): Parcelable{
    companion object {
        fun fromEntity(entity: UserEntity): User {
            return User(
                firebaseId = entity.firebaseId,
                name = entity.name,
                email = entity.email,
                role = entity.role,
            )
        }
    }
    fun toJson(): UserJson {
        return UserJson(
            firebaseId = firebaseId,
            name = name,
            email = email,
        )
    }
    fun toEntity(): UserEntity {
        return UserEntity(
            firebaseId = firebaseId,
            name = name,
            email = email,
            role = role
        )
    }
}

