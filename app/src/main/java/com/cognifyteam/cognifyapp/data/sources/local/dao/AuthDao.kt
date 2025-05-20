package com.cognifyteam.cognifyapp.data.sources.local.dao

import androidx.room.Dao
import androidx.room.Insert
import com.cognifyteam.cognifyapp.data.models.UserEntity

@Dao
interface AuthDao {
    @Insert
    suspend fun register(user:UserEntity)
}