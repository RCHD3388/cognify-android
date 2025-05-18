package com.cognifyteam.cognifyapp.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.cognifyteam.cognifyapp.data.local.model.UserEntity

@Dao
interface UserDao {


    @Insert()
    suspend fun register(user : UserEntity)

    @Query("SELECT * FROM users WHERE firebaseId = :firebaseId LIMIT 1")
    fun getUser(firebaseId : String) : LiveData<UserEntity>
}