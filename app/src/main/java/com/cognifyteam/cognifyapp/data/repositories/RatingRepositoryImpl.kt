package com.cognifyteam.cognifyapp.data.repositories

import com.cognifyteam.cognifyapp.data.models.Rating
import com.cognifyteam.cognifyapp.data.models.RatingEntity
import com.cognifyteam.cognifyapp.data.sources.local.datasources.LocalRatingDataSource
import com.cognifyteam.cognifyapp.data.sources.remote.PostRatingRequest
import com.cognifyteam.cognifyapp.data.sources.remote.course.RemoteRatingDataSource

interface RatingRepository {
    /**
     * Mengambil semua rating untuk sebuah kursus.
     * Menerapkan strategi network-first, fallback-to-cache.
     */
    suspend fun getRatings(courseId: String): Result<List<Rating>>

    /**
     * Mengirim rating baru ke server.
     */
    suspend fun postRating(courseId: String, firebaseId: String, rating: Int, comment: String?): Result<Unit>
    suspend fun getMyRating(courseId: String, firebaseId: String): Result<Rating?>
}

class RatingRepositoryImpl(
    private val localDataSource: LocalRatingDataSource,
    private val remoteDataSource: RemoteRatingDataSource
) : RatingRepository {

    override suspend fun getRatings(courseId: String): Result<List<Rating>> {
        return try {
            val response = remoteDataSource.getRatings(courseId)
            val ratings = response.data.ratings.map { Rating.fromJson(it) }

            // Simpan ke cache
            if (ratings.isNotEmpty()) {
                val ratingEntities = ratings.map { rating ->
                    // Anda perlu membuat mapper toEntity untuk Rating
                    RatingEntity(
                        id = rating.id,
                        rating = rating.rating,
                        comment = rating.comment,
                        authorName = rating.authorName,
                        courseId = courseId,
                        userId = "" // userId tidak ada di respons, bisa dikosongkan
                    )
                }
                localDataSource.upsertRatings(ratingEntities)
            }
            Result.success(ratings)
        } catch (e: Exception) {
            // Jika network gagal, ambil dari cache
            try {
                val cachedEntities = localDataSource.getRatingsForCourse(courseId)
                // Anda perlu mapper fromEntity untuk Rating
                val cachedRatings = cachedEntities.map {
                    Rating(id = it.id, rating = it.rating, comment = it.comment, authorName = it.authorName)
                }
                Result.success(cachedRatings)
            } catch (cacheError: Exception) {
                Result.failure(cacheError)
            }
        }
    }

    override suspend fun postRating(courseId: String, firebaseId: String, rating: Int, comment: String?): Result<Unit> {
        return try {
            val request = PostRatingRequest(rating, comment, firebaseId)
            remoteDataSource.postRating(courseId, request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyRating(courseId: String, firebaseId: String): Result<Rating?> {
        return try {
            val response = remoteDataSource.getMyRating(courseId, firebaseId)
            // Jika response.data adalah null, `let` tidak akan berjalan, dan hasilnya null.
            // Jika ada data, ia akan di-map menjadi objek Rating.
            val rating = response.data?.let { Rating.fromJson(it) }
            Result.success(rating)
        } catch (e: Exception) {
            // Kita bisa coba ambil dari cache jika mau, atau langsung failure.
            // Untuk kasus ini, failure lebih sederhana.
            Result.failure(e)
        }
    }
}