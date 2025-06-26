package com.cognifyteam.cognifyapp.data.sources.local.dao

import androidx.room.*
import com.cognifyteam.cognifyapp.data.models.FollowsCrossRef
import com.cognifyteam.cognifyapp.data.models.UserWithFollowers
import com.cognifyteam.cognifyapp.data.models.UserWithFollowing

@Dao
interface FollowDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFollow(follow: FollowsCrossRef)

    @Delete
    suspend fun deleteFollow(follow: FollowsCrossRef)

    // Untuk sinkronisasi: hapus semua data following dari seorang user
    @Query("DELETE FROM FollowsCrossRef WHERE followerId = :userId")
    suspend fun clearFollowingForUser(userId: String)

    @Transaction
    @Query("SELECT * FROM users WHERE firebaseId = :userId")
    suspend fun getFollowing(userId: String): UserWithFollowing?

    @Transaction
    @Query("SELECT * FROM users WHERE firebaseId = :userId")
    suspend fun getFollowers(userId: String): UserWithFollowers?
}