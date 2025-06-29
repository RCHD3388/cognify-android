package com.cognifyteam.cognifyapp.data.sources.local.datasources

import android.util.Log
import com.cognifyteam.cognifyapp.data.models.LearningPath
import com.cognifyteam.cognifyapp.data.models.LearningPathEntity
import com.cognifyteam.cognifyapp.data.models.SmartLike
import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.data.sources.local.AppDatabase

interface LocalSmartDataSource {
    suspend fun insertNewLearningPath(smart: LearningPath): LearningPath
    suspend fun getAll(): List<LearningPath>
    suspend fun likeSmart(smartId: Int, userId: String): Int
    suspend fun unlikeSmart(smartId: Int, userId: String): Int
}

class LocalSmartDataSourceImpl(
    private val db: AppDatabase
): LocalSmartDataSource{
    override suspend fun insertNewLearningPath(smart: LearningPath): LearningPath {
        db.smartDao().insertOrReplace(smart.toEntity())
        db.smartLikeDao().insertOrReplace(smart.toLikesEntity())
        db.smartCommentDao().insertOrReplace(smart.toCommentsEntity())
        db.smartStepDao().insertOrReplace(smart.toStepsEntity())

        return smart
    }

    override suspend fun getAll(): List<LearningPath> {
        val learningPaths = db.smartDao().getAllSmarts()
        return learningPaths.map {
            LearningPath.fromEntity(it,
                db.smartLikeDao().getOneSmartLikes(it.id),
                db.smartCommentDao().getOneSmartComments(it.id),
                db.smartStepDao().getOneSmartSteps(it.id)
            )
        }
    }

    override suspend fun unlikeSmart(smartId: Int, userId: String): Int {
        val learningPaths = db.smartLikeDao().delete(smartId = smartId, userId = userId)
        return learningPaths
    }

    override suspend fun likeSmart(smartId: Int, userId: String): Int {
        db.smartLikeDao().insertOrReplace(listOf(SmartLike(smartId = smartId, userId = userId)))
        return 1
    }
}