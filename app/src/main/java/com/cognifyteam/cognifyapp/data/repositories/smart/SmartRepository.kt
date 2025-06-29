package com.cognifyteam.cognifyapp.data.repositories.smart

import android.util.Log
import com.cognifyteam.cognifyapp.data.AppContainer
import com.cognifyteam.cognifyapp.data.AppContainerImpl
import com.cognifyteam.cognifyapp.data.models.Course
import com.cognifyteam.cognifyapp.data.models.GenerateLearningPathPayloadJson
import com.cognifyteam.cognifyapp.data.models.GeneratedLearningPath
import com.cognifyteam.cognifyapp.data.models.LearningPath
import com.cognifyteam.cognifyapp.data.models.LikedRes
import com.cognifyteam.cognifyapp.data.models.SaveLearningPathPayload
import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.data.models.UserCourseCrossRef
import com.cognifyteam.cognifyapp.data.models.UserJson
import com.cognifyteam.cognifyapp.data.sources.local.datasources.LocalAuthDataSource
import com.cognifyteam.cognifyapp.data.sources.local.datasources.LocalSmartDataSource
import com.cognifyteam.cognifyapp.data.sources.remote.auth.RemoteAuthDataSource
import com.cognifyteam.cognifyapp.data.sources.remote.smart.RemoteSmartDataSource
import com.cognifyteam.cognifyapp.data.sources.remote.smart.RemoteSmartDataSourceImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

interface SmartRepository {
    suspend fun generateNewLP(topic: String, level: String, additional_prompt: String): Result<GeneratedLearningPath>
    suspend fun saveNewLP(userId: String, title: String, learningPaths: GeneratedLearningPath): Result<String>
    suspend fun getAllLearningPaths(): Result<List<LearningPath>>
    suspend fun likePath(smartId: Int, userId: String): Result<String>
}

class SmartRepositoryImpl(
    private val localSmartDataSource: LocalSmartDataSource,
    private val remoteSmartDataSourceImpl: RemoteSmartDataSourceImpl
): SmartRepository {

    override suspend fun generateNewLP(
        topic: String,
        level: String,
        additional_prompt: String
    ): Result<GeneratedLearningPath> {
        return remoteSmartDataSourceImpl.generate(GenerateLearningPathPayloadJson(topic, level, additional_prompt))
    }

    override suspend fun saveNewLP(
        userId: String,
        title: String,
        learningPaths: GeneratedLearningPath
    ): Result<String> {
        try {
            val response = remoteSmartDataSourceImpl.save(SaveLearningPathPayload(title,
                learningPaths.mainDescription, userId, learningPaths.level, learningPaths.tags, learningPaths.paths))

            response.fold(
                onSuccess = {
                    localSmartDataSource.insertNewLearningPath(it)
                    Log.d("Save new LP", "${it}")
                    return  Result.success("Your new learning path saved")
                },
                onFailure = {
                    return Result.failure(Exception("Save failed. Please check your internet connection."))
                }
            )
        }catch (e: Exception){
            return Result.success("Berhasil menambahkan smart learning path")
        }
    }

    override suspend fun getAllLearningPaths(): Result<List<LearningPath>> {
        return try {
            // --- JALUR SUKSES (ONLINE) ---

            // 1. Ambil data mentah (DTO) dari remote/API
            val remoteData = remoteSmartDataSourceImpl.getAll() // Misal: mengembalikan List<LearningPathJson>
            remoteData.onSuccess{
                it.forEach {
                    localSmartDataSource.insertNewLearningPath(it)
                }
                Result.success(it)
            }
            try {
                val localData = localSmartDataSource.getAll()
                Result.success(localData)
            } catch (dbException: Exception) {
                // Jika terjadi error saat mengakses DB, kembalikan exception dari DB
                Result.failure(Exception("Terjadi kesalahan, coba lagi nanti"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Terjadi kesalahan, coba lagi nanti"))
        }
    }

    override suspend fun likePath(smartId: Int, userId: String): Result<String> {
        return try {
            val likedRes = remoteSmartDataSourceImpl.likeSmart(smartId, userId)
            likedRes.fold(
                onSuccess = {
                    if(it.liked){
                        localSmartDataSource.likeSmart(smartId, userId, it.likeId)
                        Result.success(it.likeId.toString())
                    }else{
                        localSmartDataSource.unlikeSmart(smartId, userId, it.likeId)
                        Result.success("Successfully unliked learning paths")
                    }
                },
                onFailure = {
                    Result.failure(Exception(it.message))
                }
            )
        } catch (e: Exception){
            Result.failure(Exception("Terjadi kesalahan, coba lagi nanti"))
        }
    }
}