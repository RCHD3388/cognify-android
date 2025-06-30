package com.cognifyteam.cognifyapp.data.sources.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cognifyteam.cognifyapp.data.models.LearningPathStepEntity

@Dao
interface SmartStepDao {
    @Insert
    suspend fun insert(smartStep: LearningPathStepEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(smartLike: List<LearningPathStepEntity>)

    @Query("SELECT * FROM smart_steps")
    suspend fun getAllSmartSteps(): List<LearningPathStepEntity>?

    @Query("SELECT * FROM smart_steps WHERE smartId = :smartId")
    suspend fun getOneSmartSteps(smartId: Int): List<LearningPathStepEntity>

    @Query("DELETE FROM smart_steps WHERE smartId = :smartId")
    suspend fun deleteAllById(smartId: Int)

    @Query("DELETE FROM smart_steps")
    suspend fun clearAll()
}