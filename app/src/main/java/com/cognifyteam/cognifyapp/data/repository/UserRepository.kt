package com.cognifyteam.cognifyapp.data.repository

import com.cognifyteam.cognifyapp.model.User

interface UserRepository {
    suspend fun getUser(firebaseId: String): Result<User>
    suspend fun saveUser(user: User)
}