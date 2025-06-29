package com.cognifyteam.cognifyapp.data.sources.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cognifyteam.cognifyapp.data.models.RatingEntity

@Dao
interface RatingDao {
    /**
     * Menyimpan atau memperbarui daftar rating.
     * Jika sebuah rating dengan `id` yang sama sudah ada, ia akan diganti.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRatings(ratings: List<RatingEntity>)

    /**
     * Mengambil semua rating untuk sebuah kursus tertentu dari database.
     * Diurutkan dari yang paling baru.
     * @param courseId ID dari kursus yang ratingnya akan diambil.
     */
    @Query("SELECT * FROM ratings WHERE courseId = :courseId ORDER BY id DESC") // Asumsi createdAt tidak ada, jadi pakai id
    suspend fun getRatingsForCourse(courseId: String): List<RatingEntity>
}