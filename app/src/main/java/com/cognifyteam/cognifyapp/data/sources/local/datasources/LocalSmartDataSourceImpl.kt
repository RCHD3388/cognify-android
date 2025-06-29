package com.cognifyteam.cognifyapp.data.sources.local.datasources

import android.util.Log
import com.cognifyteam.cognifyapp.data.models.LearningPath
import com.cognifyteam.cognifyapp.data.models.LearningPathEntity
import com.cognifyteam.cognifyapp.data.models.SmartComment
import com.cognifyteam.cognifyapp.data.models.SmartLike
import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.data.sources.local.AppDatabase

interface LocalSmartDataSource {
    suspend fun insertNewLearningPath(smart: LearningPath): LearningPath
    suspend fun getAll(): List<LearningPath>
    suspend fun likeSmart(smartId: Int, userId: String, id: Int): Int
    suspend fun unlikeSmart(smartId: Int, userId: String, id: Int): Int
    suspend fun getOne(id: Int): LearningPath?
    suspend fun addComment(comment: SmartComment): SmartComment
    suspend fun deletePath(smartId: Int): Int
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

    override suspend fun unlikeSmart(smartId: Int, userId: String, id: Int): Int {
        val learningPaths = db.smartLikeDao().delete(SmartLike(smartId = smartId, userId = userId, id = id))
        return learningPaths
    }

    override suspend fun likeSmart(smartId: Int, userId: String, id: Int): Int {
        db.smartLikeDao().insertOrReplace(listOf(SmartLike(smartId = smartId, userId = userId, id = id)))
        return 1
    }

    override suspend fun getOne(id: Int): LearningPath? {
        val result = db.smartDao().getSmartById(id);
        if(result == null){
            return null
        }else{
            return LearningPath.fromEntity(
                result,
                db.smartLikeDao().getOneSmartLikes(result.id),
                db.smartCommentDao().getOneSmartComments(result.id),
                db.smartStepDao().getOneSmartSteps(result.id)
            )
        }
    }

    override suspend fun addComment(comment: SmartComment): SmartComment {
        db.smartCommentDao().insert(comment)
        return comment
    }

    override suspend fun deletePath(smartId: Int): Int {
        db.smartLikeDao().deleteAllById(smartId)
        db.smartCommentDao().deleteAllById(smartId)
        db.smartStepDao().deleteAllById(smartId)
        db.smartDao().deleteById(smartId)
        return smartId
    }
}