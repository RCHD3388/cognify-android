package com.cognifyteam.cognifyapp

import android.app.Application
import com.cognifyteam.cognifyapp.data.AppContainer
import com.cognifyteam.cognifyapp.data.AppContainerImpl

class CognifyApplication: Application() {
    companion object {

    }
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this);
    }
}