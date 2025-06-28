package com.cognifyteam.cognifyapp.data.sources.local.dao

// File: data/local/SectionDao.kt


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cognifyteam.cognifyapp.data.models.SectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SectionDao {

    /**
     * Mengambil semua section untuk course tertentu, diurutkan berdasarkan posisi.
     * Menggunakan Flow agar UI otomatis update jika ada perubahan.
     */
    @Query("SELECT * FROM sections WHERE courseId = :courseId ORDER BY position ASC")
    fun getSectionsForCourse(courseId: String): Flow<List<SectionEntity>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sections: List<SectionEntity>)



}