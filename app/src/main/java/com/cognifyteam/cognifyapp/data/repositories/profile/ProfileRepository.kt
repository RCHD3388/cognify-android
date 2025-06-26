package com.cognifyteam.cognifyapp.data.repositories.profile

import com.cognifyteam.cognifyapp.data.models.User

interface ProfileRepository {
    suspend fun getProfile(firebaseId: String): Result<User>
    suspend fun updateProfile(firebaseId: String, name: String, description: String): Result<User>
}