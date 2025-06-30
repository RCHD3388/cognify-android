package com.cognifyteam.cognifyapp.data.repositories

import android.util.Log
import com.cognifyteam.cognifyapp.data.models.Transaction
import com.cognifyteam.cognifyapp.data.sources.local.datasources.LocalTransactionDataSource
import com.cognifyteam.cognifyapp.data.sources.remote.transaction.RemoteTransactionDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception

interface TransactionRepository {
    fun getUserTransactions(userId: String): Flow<Result<List<Transaction>>>
}

class TransactionRepositoryImpl(
    private val localDataSource: LocalTransactionDataSource,
    private val remoteDataSource: RemoteTransactionDataSource
) : TransactionRepository {

    override fun getUserTransactions(userId: String): Flow<Result<List<Transaction>>> = flow {
        // Kode di dalam 'flow' builder ini berjalan di dalam coroutine.
        // Jadi kita BISA memanggil suspend function di sini.

        // 1. Emit data dari cache terlebih dahulu
        try {
            // Panggil suspend function dan simpan hasilnya di variabel
            val cachedEntities = localDataSource.getUserTransactions(userId)
            // Lakukan pemetaan
            val domainModels = cachedEntities.map { entity -> Transaction.fromEntity(entity) }
            // Panggil emit dengan hasil yang sudah dipetakan
            emit(Result.success(domainModels))
        } catch (e: Exception) {
            Log.e("TransactionRepo", "Failed to load from cache", e)
            // Jangan hentikan flow, kita masih akan mencoba dari network
        }

        // 2. Selalu coba ambil data terbaru dari network
        try {
            val apiResponse = remoteDataSource.getUserTransactions(userId)

            if (apiResponse.status == "success") {
                val transactionsFromNetwork = apiResponse.data.data

                // Lakukan pemetaan dari JSON ke Entity
                val transactionEntities = transactionsFromNetwork.map { json ->
                    Transaction.fromJson(json).toEntity(userId)
                }

                // 3. Simpan data baru ke DB
                localDataSource.clearUserTransactions(userId)
                localDataSource.insertAll(transactionEntities)

                // 4. Emit data terbaru dari DB
                // Panggil suspend function lagi untuk mendapatkan data yang baru saja disimpan
                val newCachedEntities = localDataSource.getUserTransactions(userId)
                // Lakukan pemetaan lagi
                val newDomainModels = newCachedEntities.map { entity -> Transaction.fromEntity(entity) }
                // Panggil emit dengan data yang sudah di-refresh
                emit(Result.success(newDomainModels))

            } else {
                Log.w("TransactionRepo", "API returned non-success: ${apiResponse.data.message}")
                // Di sini kita tidak emit error, karena mungkin data dari cache sudah cukup
            }
        } catch (e: Exception) {
            // 5. Jika network gagal, kita sudah emit dari cache.
            Log.e("TransactionRepo", "Network fetch failed", e)
            // Jika Anda ingin UI menampilkan pesan error network, Anda bisa emit di sini:
            // emit(Result.failure(e))
        }
    }
}