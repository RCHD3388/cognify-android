package com.cognifyteam.cognifyapp.data.sources.local.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cognifyteam.cognifyapp.data.models.TransactionEntity

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<TransactionEntity>)

    @Query("SELECT * FROM transactions WHERE user_id = :userId ORDER BY createdAt DESC")
    suspend fun getUserTransactions(userId: String): List<TransactionEntity>

    @Query("DELETE FROM transactions WHERE user_id = :userId")
    suspend fun clearUserTransactions(userId: String)
}