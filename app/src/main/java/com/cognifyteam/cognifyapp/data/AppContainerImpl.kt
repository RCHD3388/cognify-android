package com.cognifyteam.cognifyapp.data

import android.content.Context
import com.cognifyteam.cognifyapp.data.repositories.auth.AuthRepository
import com.cognifyteam.cognifyapp.data.repositories.auth.AuthRepositoryImpl
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
    ).baseUrl("http://10.0.2.2:3000/").build()

    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(


        )
    }
}