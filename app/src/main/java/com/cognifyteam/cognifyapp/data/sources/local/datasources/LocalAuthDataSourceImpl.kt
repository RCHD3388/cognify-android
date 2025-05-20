package com.cognifyteam.cognifyapp.data.sources.local.datasources

import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.data.sources.local.AppDatabase

class LocalAuthDataSourceImpl(
    private val db: AppDatabase
): LocalAuthDataSource {
    override suspend fun register(firebaseId: String, name: String, email: String): User {
        val user = User(firebaseId, name)
        db.userDao().register(user.toEntity())
        return user
    }
}