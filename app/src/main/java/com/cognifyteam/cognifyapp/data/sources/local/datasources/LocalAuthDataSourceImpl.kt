package com.cognifyteam.cognifyapp.data.sources.local.datasources

import android.util.Log
import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.data.sources.local.AppDatabase

interface LocalAuthDataSource {
    suspend fun insertOrReplace(user: User): User
    suspend fun getCurrentUser(): User?
    suspend fun clearAll()
}

class LocalAuthDataSourceImpl(
    private val db: AppDatabase
): LocalAuthDataSource {
    override suspend fun insertOrReplace(user: User): User {
        db.authDao().insertOrReplace(user.toEntity())
        return user
    }
    override suspend fun getCurrentUser(): User? {
        // Ambil dari DAO, dan konversi dari Entity ke Model jika tidak null
        return db.authDao().getCurrentUser()?.let { User.fromEntity(it) }
    }
    override suspend fun clearAll() {
        db.authDao().clearAll()
    }
}