package com.cognifyteam.cognifyapp.data.remote.response

import com.cognifyteam.cognifyapp.data.local.model.UserEntity
import com.cognifyteam.cognifyapp.model.User

data class UserResponse(
    val status :Int,
    val message :String,
    val data : User
) {
    fun toEntity(): UserEntity {
        return  UserEntity(
            firebaseId = data.firebaseId,
            email = data.email,
            role = data.role
        )
    }
    fun toDomain(): User {
        return User(
            firebaseId = data.firebaseId,
            email = data.email,
            role = data.role
        )
    }
}
