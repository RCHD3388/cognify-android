package com.cognifyteam.cognifyapp.data.repositories.profile

import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.data.remote.responses.ProfileData
import com.cognifyteam.cognifyapp.data.sources.local.datasources.LocalProfileDataSource // <-- UBAH DEPENDENSI INI
import com.cognifyteam.cognifyapp.data.sources.remote.profile.RemoteProfileDataSource
import retrofit2.HttpException
import java.io.IOException

class ProfileRepositoryImpl(
    private val localDataSource: LocalProfileDataSource,
    private val remoteDataSource: RemoteProfileDataSource
) : ProfileRepository {

    override suspend fun getProfile(firebaseId: String): User? {
        try {
            // 1. Panggil remote data source, hasilnya adalah BaseResponse
            val baseResponse = remoteDataSource.getProfile(firebaseId)

            // 2. "Buka" lapisan-lapisan untuk mendapatkan UserDto
            val userDto = baseResponse.data.user // <-- DARI baseResponse.data.user

            // 3. Jika berhasil, konversi ke domain model
            val user = User.fromDto(userDto)

            // 4. Simpan ke database
            localDataSource.upsertUser(user.toEntity())

            // 5. Kembalikan data segar
            return user

        } catch (e: Exception) {
            // ... logika fallback ke cache tidak berubah ...
            println("Error fetching from network, falling back to cache: ${e.message}")
            val cachedUserEntity = localDataSource.getProfileById(firebaseId)
            return cachedUserEntity?.let { User.fromEntity(it) }
        }
    }
}