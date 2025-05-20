package com.cognifyteam.cognifyapp.data.sources.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cognifyteam.cognifyapp.data.models.UserEntity
import com.cognifyteam.cognifyapp.data.sources.local.dao.AuthDao

@Database(entities = [
    UserEntity::class
], version = 1)
abstract class AppDatabase : RoomDatabase() {

     abstract fun userDao(): AuthDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
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