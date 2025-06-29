package com.cognifyteam.cognifyapp.data.sources.remote.smart

import android.util.Log
import com.cognifyteam.cognifyapp.data.AppContainer
import com.cognifyteam.cognifyapp.data.AppContainerImpl
import com.cognifyteam.cognifyapp.data.models.GenerateLearningPathPayloadJson
import com.cognifyteam.cognifyapp.data.models.GeneratedLearningPath
import com.cognifyteam.cognifyapp.data.models.LearningPath
import com.cognifyteam.cognifyapp.data.models.LikedBody
import com.cognifyteam.cognifyapp.data.models.LikedRes
import com.cognifyteam.cognifyapp.data.models.SaveLearningPathPayload
import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.data.models.UserJson
import com.cognifyteam.cognifyapp.data.sources.remote.ApiResponse
import com.cognifyteam.cognifyapp.data.sources.remote.services.AuthService
import com.cognifyteam.cognifyapp.data.sources.remote.services.SmartService

interface RemoteSmartDataSource {
    suspend fun generate(payloadJson: GenerateLearningPathPayloadJson): Result<GeneratedLearningPath>
    suspend fun save(payloadJson: SaveLearningPathPayload): Result<LearningPath>
    suspend fun getAll(): Result<List<LearningPath>>
    suspend fun likeSmart(smartId: Int, userId: String): Result<LikedRes>
}

class RemoteSmartDataSourceImpl(
    private val smartService: SmartService
): RemoteSmartDataSource {
    override suspend fun generate(payloadJson: GenerateLearningPathPayloadJson): Result<GeneratedLearningPath> {
        try {
            val response = smartService.generate(payloadJson)
            if(response.isSuccessful){
                val body = response.body()
                if (body != null){
                    return Result.success(body.data.data);
                }else{
                    return Result.failure(Exception("Empty response"))
                }
            } else {
                val error = AppContainerImpl.parseErrorMessage(response.errorBody()?.string())
                return Result.failure(Exception(error ?: "Unknown error"))
            }
        } catch (e: Exception){
            return Result.failure(e)
        }
    }
    override suspend fun save(payloadJson: SaveLearningPathPayload): Result<LearningPath> {
        try {
            val response = smartService.save(payloadJson)
            if(response.isSuccessful){
                val body = response.body()
                if (body != null){
                    return Result.success(body.data.data);
                }else{
                    return Result.failure(Exception("Empty response"))
                }
            } else {
                val error = AppContainerImpl.parseErrorMessage(response.errorBody()?.string())
                return Result.failure(Exception(error ?: "Unknown error"))
            }
        } catch (e: Exception){
            return Result.failure(e)
        }
    }

    override suspend fun getAll(): Result<List<LearningPath>> {
        try {
            val response = smartService.getAll()
            if(response.isSuccessful){
                val body = response.body()
                if (body != null){
                    return Result.success(body.data.data);
                }else{
                    return Result.failure(Exception("Empty response"))
                }
            } else {
                val error = AppContainerImpl.parseErrorMessage(response.errorBody()?.string())
                return Result.failure(Exception(error ?: "Unknown error"))
            }
        } catch (e: Exception){
            return Result.failure(e)
        }
    }

    override suspend fun likeSmart(smartId: Int, userId: String): Result<LikedRes> {
        try {
            val response = smartService.likeSmart(smartId, LikedBody(userId))
            if(response.isSuccessful){
                val body = response.body()
                if (body != null){
                    return Result.success(body.data.data);
                }else{
                    return Result.failure(Exception("Empty response"))
                }
            } else {
                val error = AppContainerImpl.parseErrorMessage(response.errorBody()?.string())
                return Result.failure(Exception("Failed to like. Please check your internet connection."))
            }
        } catch (e: Exception){
            return Result.failure(Exception("Failed to like. Please check your internet connection."))
        }
    }
}