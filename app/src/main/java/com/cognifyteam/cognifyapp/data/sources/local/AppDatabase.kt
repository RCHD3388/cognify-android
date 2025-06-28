package com.cognifyteam.cognifyapp.data.sources.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cognifyteam.cognifyapp.data.models.CourseEntity
import com.cognifyteam.cognifyapp.data.models.DiscussionEntity
import com.cognifyteam.cognifyapp.data.models.FollowsCrossRef
import com.cognifyteam.cognifyapp.data.models.SectionEntity
import com.cognifyteam.cognifyapp.data.models.UserCourseCrossRef
import com.cognifyteam.cognifyapp.data.models.UserEntity
import com.cognifyteam.cognifyapp.data.sources.local.dao.AuthDao
import com.cognifyteam.cognifyapp.data.sources.local.dao.CourseDao
import com.cognifyteam.cognifyapp.data.sources.local.dao.DiscussionDao
import com.cognifyteam.cognifyapp.data.sources.local.dao.FollowDao
import com.cognifyteam.cognifyapp.data.sources.local.dao.ProfileDao
import com.cognifyteam.cognifyapp.data.sources.local.dao.SectionDao

@Database(entities = [
    UserEntity::class,
    CourseEntity::class,
    UserCourseCrossRef::class,
    DiscussionEntity::class,
    FollowsCrossRef::class,
    SectionEntity::class
], version = 8)
abstract class AppDatabase : RoomDatabase() {

     abstract fun authDao(): AuthDao
     abstract fun profileDao(): ProfileDao
     abstract fun courseDao(): CourseDao
     abstract fun discussionDao(): DiscussionDao
    abstract fun followDao(): FollowDao
    abstract fun sectionDao(): SectionDao

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