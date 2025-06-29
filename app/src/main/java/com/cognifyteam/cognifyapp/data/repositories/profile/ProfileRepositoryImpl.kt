package com.cognifyteam.cognifyapp.data.repositories.profile

import android.util.Log
import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.data.sources.local.datasources.LocalProfileDataSource
import com.cognifyteam.cognifyapp.data.sources.remote.UpdateProfileRequest
import com.cognifyteam.cognifyapp.data.sources.remote.profile.RemoteProfileDataSource

class ProfileRepositoryImpl(
    private val localDataSource: LocalProfileDataSource,
    private val remoteDataSource: RemoteProfileDataSource
) : ProfileRepository {

    override suspend fun getProfile(firebaseId: String): Result<User> {
        try {
            val baseResponse = remoteDataSource.getProfile(firebaseId)

            // "Buka" lapisan-lapisan DTO untuk mendapatkan UserJson
            // Ya, ini terlihat sedikit aneh (baseResponse.data.data),
            // tetapi ini benar-benar mencerminkan struktur JSON Anda.
            val userJson = baseResponse.data.data

            val user = User.fromJson(userJson)

            localDataSource.upsertUser(user.toEntity())

            return Result.success(user)

        } catch (e: Exception) {
            val cachedUserEntity = localDataSource.getProfileById(firebaseId)
            return if (cachedUserEntity != null) {
                // Gunakan mapper fromEntity
                Result.success(User.fromEntity(cachedUserEntity))
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun updateProfile(firebaseId: String, name: String, description: String): Result<User> {
        return try {
            val request = UpdateProfileRequest(name = name, description = description)
            // Berikan firebaseId ke remote data source
            val response = remoteDataSource.updateProfile(firebaseId, request)
            val updatedUser = User.fromJson(response.data)

            localDataSource.upsertUser(updatedUser.toEntity())
            Result.success(updatedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}