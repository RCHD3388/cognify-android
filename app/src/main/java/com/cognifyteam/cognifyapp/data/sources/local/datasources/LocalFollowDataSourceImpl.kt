package com.cognifyteam.cognifyapp.data.sources.local.datasources

import com.cognifyteam.cognifyapp.data.models.FollowsCrossRef
import com.cognifyteam.cognifyapp.data.models.UserEntity
import com.cognifyteam.cognifyapp.data.models.UserWithFollowers
import com.cognifyteam.cognifyapp.data.models.UserWithFollowing
import com.cognifyteam.cognifyapp.data.sources.local.dao.FollowDao

interface LocalFollowDataSource {
    suspend fun insertFollow(follow: FollowsCrossRef)
    suspend fun insertFollows(follows: List<FollowsCrossRef>) // Untuk sinkronisasi massal
    suspend fun deleteFollow(follow: FollowsCrossRef)
    suspend fun clearFollowingForUser(userId: String)
    suspend fun getFollowing(userId: String): UserWithFollowing?
    suspend fun getFollowers(userId: String): UserWithFollowers?
    suspend fun upsertUsers(users: List<UserEntity>)
}

class LocalFollowDataSourceImpl(
    private val followDao: FollowDao
) : LocalFollowDataSource {

    override suspend fun upsertUsers(users: List<UserEntity>) {
        followDao.upsertUsers(users)
    }

    override suspend fun insertFollow(follow: FollowsCrossRef) {
        followDao.insertFollow(follow)
    }

    override suspend fun insertFollows(follows: List<FollowsCrossRef>) {
        // DAO tidak punya fungsi ini, jadi kita bisa iterasi atau buat fungsi baru di DAO
        // Untuk sekarang, kita iterasi saja:
        follows.forEach { followDao.insertFollow(it) }
    }

    override suspend fun deleteFollow(follow: FollowsCrossRef) {
        followDao.deleteFollow(follow)
    }

    override suspend fun clearFollowingForUser(userId: String) {
        followDao.clearFollowingForUser(userId)
    }

    override suspend fun getFollowing(userId: String): UserWithFollowing? {
        return followDao.getFollowing(userId)
    }

    override suspend fun getFollowers(userId: String): UserWithFollowers? {
        return followDao.getFollowers(userId)
    }
}