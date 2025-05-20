package com.cognifyteam.cognifyapp

import android.app.Application
import com.cognifyteam.cognifyapp.data.AppContainer
import com.cognifyteam.cognifyapp.data.AppContainerImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class CognifyApplication: Application() {
    companion object {

    }
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this);
    }
}