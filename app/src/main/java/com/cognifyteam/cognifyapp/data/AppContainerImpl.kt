package com.cognifyteam.cognifyapp.data

import android.content.Context
import android.net.ConnectivityManager
import com.cognifyteam.cognifyapp.data.repositories.auth.AuthRepository
import com.cognifyteam.cognifyapp.data.repositories.auth.AuthRepositoryImpl
import com.cognifyteam.cognifyapp.data.sources.local.AppDatabase
import com.cognifyteam.cognifyapp.data.sources.local.datasources.LocalAuthDataSource
import com.cognifyteam.cognifyapp.data.sources.local.datasources.LocalAuthDataSourceImpl
import com.cognifyteam.cognifyapp.data.sources.remote.ErrorResponse
import com.cognifyteam.cognifyapp.data.sources.remote.auth.RemoteAuthDataSourceImpl
import com.cognifyteam.cognifyapp.data.sources.remote.services.AuthService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

interface AppContainer{
    val authRepository: AuthRepository
}

class AppContainerImpl(private val applicationContext: Context) : AppContainer {
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val retrofit = Retrofit.Builder().addConverterFactory(
        MoshiConverterFactory.create(moshi)
    ).baseUrl("http://10.0.2.2:3000/api/v1/").build()

    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(
            LocalAuthDataSourceImpl(AppDatabase.getInstance(applicationContext)),
            RemoteAuthDataSourceImpl(retrofit.create(AuthService::class.java))
        )
    }

    companion object {
        fun parseErrorMessage(json: String?): String? {
            return try {
                val moshi = Moshi.Builder().build()
                val adapter = moshi.adapter(ErrorResponse::class.java)
                adapter.fromJson(json ?: "")?.message
            } catch (e: Exception) {
                null
            }
        }

        fun isInternetAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            return connectivityManager?.activeNetworkInfo?.isConnectedOrConnecting == true
        }
    }
}