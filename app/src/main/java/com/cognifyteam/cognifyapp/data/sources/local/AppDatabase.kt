package com.cognifyteam.cognifyapp.data.sources.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cognifyteam.cognifyapp.data.models.CourseEntity
import com.cognifyteam.cognifyapp.data.models.UserCourseCrossRef
import com.cognifyteam.cognifyapp.data.models.UserEntity
import com.cognifyteam.cognifyapp.data.sources.local.dao.AuthDao
import com.cognifyteam.cognifyapp.data.sources.local.dao.CourseDao
import com.cognifyteam.cognifyapp.data.sources.local.dao.ProfileDao

@Database(entities = [
    UserEntity::class,
    CourseEntity::class,
    UserCourseCrossRef::class
], version = 3)
abstract class AppDatabase : RoomDatabase() {

     abstract fun authDao(): AuthDao
     abstract fun profileDao(): ProfileDao
     abstract fun courseDao(): CourseDao

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