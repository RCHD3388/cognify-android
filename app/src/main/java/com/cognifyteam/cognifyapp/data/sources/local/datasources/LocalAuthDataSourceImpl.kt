package com.cognifyteam.cognifyapp.data.sources.local.datasources

import android.util.Log
import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.data.sources.local.AppDatabase

interface LocalAuthDataSource {
    suspend fun register(user: User): User
    suspend fun insertOrReplace(user: User): User
}

class LocalAuthDataSourceImpl(
    private val db: AppDatabase
): LocalAuthDataSource {
    override suspend fun register(user: User): User {
        db.authDao().insert(user.toEntity())
        return user
    }
    override suspend fun insertOrReplace(user: User): User {
        db.authDao().insertOrReplace(user.toEntity())
        return user
    }
}