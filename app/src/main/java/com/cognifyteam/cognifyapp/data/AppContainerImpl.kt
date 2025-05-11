package com.cognifyteam.cognifyapp.data

import android.content.Context

interface AppContainer{

}

class AppContainerImpl(private val applicationContext: Context) : AppContainer {

}