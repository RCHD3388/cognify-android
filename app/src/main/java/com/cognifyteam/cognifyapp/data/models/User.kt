package com.cognifyteam.cognifyapp.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cognifyteam.cognifyapp.data.remote.responses.UserDto
import kotlinx.parcelize.Parcelize

// Tidak ada perubahan di sini
@Entity(tableName = "users")
data class UserEntity (
    @PrimaryKey(autoGenerate = false)
    val firebaseId: String,
    var name:String,
    var email: String,
    var role: String // Tambahkan role agar sesuai
)

// Tidak ada perubahan di sini
@Parcelize
data class User (
    val firebaseId: String,
    val name: String,
    val email: String,
    val role: String // Tambahkan role agar sesuai
): Parcelable {
    // Companion object sekarang melakukan mapping dari/ke DTO dan Entity
    companion object {
        fun fromDto(dto: UserDto): User {
            return User(
                firebaseId = dto.firebaseId,
                name = dto.name,
                email = dto.email,
                role = dto.role
            )
        }
        fun fromEntity(entity: UserEntity): User {
            return User(
                firebaseId = entity.firebaseId,
                name = entity.name,
                email = entity.email,
                role = entity.role
            )
        }
    }
    fun toEntity(): UserEntity {
        return UserEntity(
            firebaseId = firebaseId,
            name = name,
            email = email,
            role = role
        )
    }

    fun toJson(): UserDto {
        return UserDto(
            firebaseId = this.firebaseId,
            name = this.name,
            email = this.email,
            role = this.role
        )
    }
}