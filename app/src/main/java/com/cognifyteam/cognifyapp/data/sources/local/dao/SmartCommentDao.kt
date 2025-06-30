package com.cognifyteam.cognifyapp.data.sources.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cognifyteam.cognifyapp.data.models.LearningPathEntity
import com.cognifyteam.cognifyapp.data.models.SmartComment
import com.cognifyteam.cognifyapp.data.models.SmartLike
import com.cognifyteam.cognifyapp.data.models.UserEntity

@Dao
interface SmartCommentDao {
    @Insert
    suspend fun insert(smartComment: SmartComment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(smartComment: List<SmartComment>)

    @Query("SELECT * FROM smart_comments")
    suspend fun getAllSmartComments(): List<SmartComment>?

    @Query("SELECT * FROM smart_comments WHERE smartId = :smartId")
    suspend fun getOneSmartComments(smartId: Int): List<SmartComment>

    @Query("DELETE FROM smart_comments WHERE smartId = :smartId")
    suspend fun deleteAllById(smartId: Int)

    @Query("DELETE FROM smart_comments")
    suspend fun clearAll()
}