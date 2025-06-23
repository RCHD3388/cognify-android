package com.cognifyteam.cognifyapp.data.sources.local.datasources

import com.cognifyteam.cognifyapp.data.models.UserEntity
import com.cognifyteam.cognifyapp.data.sources.local.AppDatabase

interface LocalProfileDataSource {
    suspend fun getProfileById(firebaseId: String): UserEntity?

    suspend fun upsertUser(user: UserEntity)
}

class LocalProfileDataSourceImpl(
    private val db: AppDatabase
) : LocalProfileDataSource {

    override suspend fun getProfileById(firebaseId: String): UserEntity? {
        return db.profileDao().getProfileById(firebaseId)
    }

    override suspend fun upsertUser(user: UserEntity) {
        db.profileDao().upsertUser(user)
    }
}