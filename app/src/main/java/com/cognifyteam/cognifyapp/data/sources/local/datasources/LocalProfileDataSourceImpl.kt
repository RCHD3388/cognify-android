package com.cognifyteam.cognifyapp.data.sources.local.datasources

import com.cognifyteam.cognifyapp.data.models.UserEntity
import com.cognifyteam.cognifyapp.data.sources.local.AppDatabase
import com.cognifyteam.cognifyapp.data.sources.local.dao.ProfileDao
import kotlinx.coroutines.flow.Flow

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