package com.cognifyteam.cognifyapp.model

import com.cognifyteam.cognifyapp.data.local.model.UserEntity

data class User(
    val firebaseId: Int,
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
