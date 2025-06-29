package com.cognifyteam.cognifyapp.data.sources.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cognifyteam.cognifyapp.data.models.LearningPathEntity
import com.cognifyteam.cognifyapp.data.models.SmartLike
import com.cognifyteam.cognifyapp.data.models.UserEntity

@Dao
interface SmartLikeDao {
    @Insert
    suspend fun insert(smartLike: SmartLike)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(smartLike: List<SmartLike>)

    @Query("SELECT * FROM smart_likes")
    suspend fun getAllSmartLikes(): List<SmartLike>?

    @Query("SELECT * FROM smart_likes WHERE smartId = :smartId")
    suspend fun getOneSmartLikes(smartId: Int): List<SmartLike>

    @Query("DELETE FROM smart_likes")
    suspend fun clearAll()

    @Delete
    suspend fun delete(smartLike: SmartLike): Int
}
