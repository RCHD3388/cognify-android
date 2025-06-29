package com.cognifyteam.cognifyapp.data.sources.local.datasources

import com.cognifyteam.cognifyapp.data.models.TransactionEntity
import com.cognifyteam.cognifyapp.data.sources.local.dao.TransactionDao

interface LocalTransactionDataSource {
    suspend fun getUserTransactions(userId: String): List<TransactionEntity>
    suspend fun insertAll(transactions: List<TransactionEntity>)
    suspend fun clearUserTransactions(userId: String)
}

class LocalTransactionDataSourceImpl(
    private val transactionDao: TransactionDao
) :  LocalTransactionDataSource {
    override suspend fun getUserTransactions(userId: String): List<TransactionEntity> {
        return transactionDao.getUserTransactions(userId)
    }

    override suspend fun insertAll(transactions: List<TransactionEntity>) {
        transactionDao.insertAll(transactions)
    }

    override suspend fun clearUserTransactions(userId: String) {
        transactionDao.clearUserTransactions(userId)
    }
}