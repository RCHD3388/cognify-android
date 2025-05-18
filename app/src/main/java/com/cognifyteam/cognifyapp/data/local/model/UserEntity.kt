package com.cognifyteam.cognifyapp.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cognifyteam.cognifyapp.model.User


@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val firebaseId: Int,
    val email: String,
    val role: String
){
    fun toEntity(): UserEntity {
        return  UserEntity(
            firebaseId = this.firebaseId,
            email = this.email,
            role = this.role
        )
    }
    fun toDomain(): User {
        return User(
            firebaseId = this.firebaseId,
            email = this.email,
            role = this.role
        )
    }
}