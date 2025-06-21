package com.cognifyteam.cognifyapp.data.sources.local.datasources

import com.cognifyteam.cognifyapp.data.models.UserEntity

interface LocalProfileDataSource {
    suspend fun getProfileById(firebaseId: String): UserEntity?

    suspend fun upsertUser(user: UserEntity)
}