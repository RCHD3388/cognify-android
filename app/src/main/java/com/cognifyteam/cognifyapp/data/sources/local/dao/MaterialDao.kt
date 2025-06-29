package com.cognifyteam.cognifyapp.data.sources.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cognifyteam.cognifyapp.data.models.MaterialEntity

@Dao
interface MaterialDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(materials: List<MaterialEntity>)

    @Query("SELECT * FROM materials WHERE sectionId = :sectionId")
    suspend fun getMaterialsForSection(sectionId: String): List<MaterialEntity>

}