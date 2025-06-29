package com.cognifyteam.cognifyapp.data.sources.local.datasources

import com.cognifyteam.cognifyapp.data.models.RatingEntity
import com.cognifyteam.cognifyapp.data.sources.local.AppDatabase
import com.cognifyteam.cognifyapp.data.sources.local.dao.RatingDao

interface LocalRatingDataSource {
    suspend fun getRatingsForCourse(courseId: String): List<RatingEntity>
    suspend fun upsertRatings(ratings: List<RatingEntity>)
}

class LocalRatingDataSourceImpl(
    private val db: AppDatabase
) : LocalRatingDataSource {

    override suspend fun getRatingsForCourse(courseId: String): List<RatingEntity> {
        return db.ratingDao().getRatingsForCourse(courseId)
    }

    override suspend fun upsertRatings(ratings: List<RatingEntity>) {
        db.ratingDao().upsertRatings(ratings)
    }
}