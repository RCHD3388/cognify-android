package com.cognifyteam.cognifyapp

import android.app.Application
import com.cognifyteam.cognifyapp.data.AppContainer
import com.cognifyteam.cognifyapp.data.AppContainerImpl
import com.cognifyteam.cognifyapp.data.local.AppDatabase

class CognifyApplication: Application() {
    companion object {
        lateinit var db:AppDatabase
    }
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this);
        db = AppDatabase.getInstance(baseContext)
    }
}