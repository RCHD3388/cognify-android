package com.cognifyteam.cognifyapp.data.sources.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.cognifyteam.cognifyapp.data.models.UserEntity

@Dao
interface AuthDao {
    @Insert
    suspend fun insert(user:UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(user: UserEntity)
}