package com.cognifyteam.cognifyapp.data.repositories

import com.cognifyteam.cognifyapp.data.models.FollowsCrossRef
import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.data.sources.local.datasources.LocalFollowDataSource
import com.cognifyteam.cognifyapp.data.sources.remote.users.RemoteFollowDataSource

interface FollowRepository {
    suspend fun getFollowing(userId: String): Result<List<User>>
    suspend fun getFollowers(userId: String): Result<List<User>>
    suspend fun followUser(followerId: String, userIdToFollow: String): Result<Unit>
    suspend fun unfollowUser(followerId: String, userIdToUnfollow: String): Result<Unit>
    suspend fun searchUsers(query: String): Result<List<User>>
}

class FollowRepositoryImpl(
    private val localDataSource: LocalFollowDataSource,
    private val remoteDataSource: RemoteFollowDataSource,
) : FollowRepository {

    override suspend fun getFollowing(userId: String): Result<List<User>> {
        try {
            // --- JALUR NETWORK ---
            val response = remoteDataSource.getFollowing(userId)
            val users = response.data.map { User.fromJson(it) }

            // --- SINKRONISASI CACHE ---
            // 1. Simpan data pengguna yang didapat ke dalam tabel 'users'
//            if (users.isNotEmpty()) {
//                localDataSource.upsertUsers(users.map { it.toEntity() })
//            }

            // 2. Bersihkan relasi 'following' yang lama untuk user ini
            localDataSource.clearFollowingForUser(userId)

            // 3. Masukkan relasi 'following' yang baru
            if (users.isNotEmpty()) {
                val crossRefs = users.map { FollowsCrossRef(followerId = userId, followingId = it.firebaseId) }
                localDataSource.insertFollows(crossRefs)
            }

            return Result.success(users)

        } catch (e: Exception) {
            // --- JALUR CACHE (FALLBACK) ---
            println("Failed to fetch 'following' from network, using cache. Error: ${e.message}")
            // Bungkus logika cache dalam Result.runCatching untuk keamanan
            return Result.runCatching {
                val userWithFollowing = localDataSource.getFollowing(userId)
                // Jika tidak ada apa-apa di cache, kembalikan list kosong
                userWithFollowing?.following?.map { User.fromEntity(it) } ?: emptyList()
            }
        }
    }

    override suspend fun getFollowers(userId: String): Result<List<User>> {
        // Logika serupa dengan getFollowing, tapi untuk followers
        // ...
        return Result.success(emptyList()) // Implementasi Lanjutan
    }

    override suspend fun followUser(followerId: String, userIdToFollow: String): Result<Unit> {
        return try {
            // Backend tidak butuh followerId, karena sudah didapat dari token otentikasi
            remoteDataSource.followUser(userIdToFollow, followerId)

            // Update cache secara optimis menggunakan parameter yang diberikan
            localDataSource.insertFollow(FollowsCrossRef(followerId = followerId, followingId = userIdToFollow))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // GUNAKAN PARAMETER 'followerId'
    override suspend fun unfollowUser(followerId: String, userIdToUnfollow: String): Result<Unit> {
        return try {
            // Backend tidak butuh followerId
            remoteDataSource.unfollowUser(userIdToUnfollow, followerId)

            // Update cache secara optimis menggunakan parameter yang diberikan
            localDataSource.deleteFollow(FollowsCrossRef(followerId = followerId, followingId = userIdToUnfollow))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchUsers(query: String): Result<List<User>> {
        // ...
        return try {
            val response = remoteDataSource.searchUsers(query)

            // "Buka" lapisan: response.data.users
            // Ini akan mengambil array kosong [] dari JSON Anda
            val userJsons = response.data.users

            // .map pada list kosong akan menghasilkan list kosong, jadi ini aman
            val users = userJsons.map { User.fromJson(it) }
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}