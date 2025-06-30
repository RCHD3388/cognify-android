package com.cognifyteam.cognifyapp.data.sources.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cognifyteam.cognifyapp.data.models.CourseEntity
import com.cognifyteam.cognifyapp.data.models.DiscussionEntity
import com.cognifyteam.cognifyapp.data.models.FollowsCrossRef
import com.cognifyteam.cognifyapp.data.models.LearningPathEntity
import com.cognifyteam.cognifyapp.data.models.LearningPathStepEntity
import com.cognifyteam.cognifyapp.data.models.RatingEntity
import com.cognifyteam.cognifyapp.data.models.SmartComment
import com.cognifyteam.cognifyapp.data.models.SmartLike
import com.cognifyteam.cognifyapp.data.models.SectionEntity
import com.cognifyteam.cognifyapp.data.models.TransactionEntity
import com.cognifyteam.cognifyapp.data.models.UserCourseCrossRef
import com.cognifyteam.cognifyapp.data.models.UserEntity
import com.cognifyteam.cognifyapp.data.sources.local.dao.AuthDao
import com.cognifyteam.cognifyapp.data.sources.local.dao.CourseDao
import com.cognifyteam.cognifyapp.data.sources.local.dao.DiscussionDao
import com.cognifyteam.cognifyapp.data.sources.local.dao.FollowDao
import com.cognifyteam.cognifyapp.data.sources.local.dao.ProfileDao
import com.cognifyteam.cognifyapp.data.sources.local.dao.RatingDao
import com.cognifyteam.cognifyapp.data.sources.local.dao.SmartCommentDao
import com.cognifyteam.cognifyapp.data.sources.local.dao.SmartDao
import com.cognifyteam.cognifyapp.data.sources.local.dao.SmartLikeDao
import com.cognifyteam.cognifyapp.data.sources.local.dao.SmartStepDao
import com.cognifyteam.cognifyapp.data.sources.local.dao.SectionDao
import com.cognifyteam.cognifyapp.data.sources.local.dao.TransactionDao

@Database(entities = [
    UserEntity::class,
    CourseEntity::class,
    UserCourseCrossRef::class,
    DiscussionEntity::class,
    FollowsCrossRef::class,
    LearningPathEntity::class,
    LearningPathStepEntity::class,
    SmartLike::class,
    SmartComment::class,
    SectionEntity::class,
    TransactionEntity::class,
    RatingEntity::class
], version = 17)

abstract class AppDatabase : RoomDatabase() {

     abstract fun authDao(): AuthDao
     abstract fun smartDao(): SmartDao
     abstract fun smartStepDao(): SmartStepDao
     abstract fun smartLikeDao(): SmartLikeDao
     abstract fun smartCommentDao(): SmartCommentDao
     abstract fun profileDao(): ProfileDao
     abstract fun courseDao(): CourseDao
     abstract fun discussionDao(): DiscussionDao
    abstract fun followDao(): FollowDao
    abstract fun sectionDao(): SectionDao
    abstract fun transactionDao(): TransactionDao
    abstract fun ratingDao(): RatingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
//        context.deleteDatabase("cognify_local_db")
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context,
                    AppDatabase::class.java, "cognify_local_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}