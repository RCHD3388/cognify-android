package com.cognifyteam.cognifyapp.data.sources.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cognifyteam.cognifyapp.data.models.LearningPathEntity
import com.cognifyteam.cognifyapp.data.models.UserEntity

@Dao
interface SmartDao {
    @Insert
    suspend fun insert(smart: LearningPathEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(smart: LearningPathEntity)

    @Query("SELECT * FROM smarts")
    suspend fun getAllSmarts(): List<LearningPathEntity>

    @Query("SELECT * FROM smarts WHERE id = :id")
    suspend fun getSmartById(id: Int): LearningPathEntity?

    @Query("DELETE FROM smarts")
    suspend fun clearAll()
}