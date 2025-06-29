package com.cognifyteam.cognifyapp.data.sources.remote.course

import com.cognifyteam.cognifyapp.data.models.RatingJson
import com.cognifyteam.cognifyapp.data.sources.remote.BaseResponse
import com.cognifyteam.cognifyapp.data.sources.remote.PostRatingRequest
import com.cognifyteam.cognifyapp.data.sources.remote.RatingListData
import com.cognifyteam.cognifyapp.data.sources.remote.services.RatingService

/**
 * Interface untuk sumber data remote yang berhubungan dengan rating.
 */
interface RemoteRatingDataSource {
    /**
     * Mengambil daftar rating untuk sebuah kursus dari API.
     */
    suspend fun getRatings(courseId: String): BaseResponse<RatingListData>

    /**
     * Mengirim rating baru ke API.
     */
    suspend fun postRating(courseId: String, request: PostRatingRequest): BaseResponse<Any>
    suspend fun getMyRating(courseId: String, firebaseId: String): BaseResponse<RatingJson?>
}

class RemoteRatingDataSourceImpl(
    private val ratingService: RatingService
) : RemoteRatingDataSource {

    override suspend fun getRatings(courseId: String): BaseResponse<RatingListData> {
        // Cukup panggil fungsi dari service dan kembalikan hasilnya.
        return ratingService.getRatings(courseId)
    }

    override suspend fun postRating(courseId: String, request: PostRatingRequest): BaseResponse<Any> {
        // Cukup panggil fungsi dari service dan kembalikan hasilnya.
        return ratingService.postRating(courseId, request)
    }

    override suspend fun getMyRating(courseId: String, firebaseId: String): BaseResponse<RatingJson?> {
        return ratingService.getMyRating(courseId, firebaseId)
    }
}