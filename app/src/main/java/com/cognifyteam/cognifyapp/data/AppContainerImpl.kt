package com.cognifyteam.cognifyapp.data

import android.content.Context
import android.net.ConnectivityManager
import com.cognifyteam.cognifyapp.data.repositories.CourseRepository
import com.cognifyteam.cognifyapp.data.repositories.CourseRepositoryImpl
import com.cognifyteam.cognifyapp.data.repositories.auth.AuthRepository
import com.cognifyteam.cognifyapp.data.repositories.auth.AuthRepositoryImpl
import com.cognifyteam.cognifyapp.data.repositories.profile.ProfileRepository
import com.cognifyteam.cognifyapp.data.repositories.profile.ProfileRepositoryImpl
import com.cognifyteam.cognifyapp.data.sources.local.AppDatabase
import com.cognifyteam.cognifyapp.data.sources.local.datasources.LocalAuthDataSource
import com.cognifyteam.cognifyapp.data.sources.local.datasources.LocalAuthDataSourceImpl
import com.cognifyteam.cognifyapp.data.sources.local.datasources.LocalCourseDataSourceImpl
import com.cognifyteam.cognifyapp.data.sources.local.datasources.LocalProfileDataSourceImpl
import com.cognifyteam.cognifyapp.data.sources.remote.ErrorResponse
import com.cognifyteam.cognifyapp.data.sources.remote.auth.RemoteAuthDataSourceImpl
import com.cognifyteam.cognifyapp.data.sources.remote.course.RemoteCourseDataSourceImpl
import com.cognifyteam.cognifyapp.data.sources.remote.profile.RemoteProfileDataSourceImpl
import com.cognifyteam.cognifyapp.data.sources.remote.services.AuthService
import com.cognifyteam.cognifyapp.data.sources.remote.services.CourseService
import com.cognifyteam.cognifyapp.data.sources.remote.services.ProfileService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

interface AppContainer{
    val authRepository: AuthRepository
    val profileRepository: ProfileRepository
    val courseRepository: CourseRepository
}

class AppContainerImpl(private val applicationContext: Context) : AppContainer {
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val retrofit = Retrofit.Builder().addConverterFactory(
        MoshiConverterFactory.create(moshi)
    ).baseUrl("http://172.21.128.1:3000/api/v1/").build()

    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(
            LocalAuthDataSourceImpl(AppDatabase.getInstance(applicationContext)),
            RemoteAuthDataSourceImpl(retrofit.create(AuthService::class.java))
        )
    }

    override val profileRepository: ProfileRepository by lazy {
        ProfileRepositoryImpl(
            LocalProfileDataSourceImpl(AppDatabase.getInstance(applicationContext)),
            RemoteProfileDataSourceImpl(retrofit.create(ProfileService::class.java))
        )
    }

    override val courseRepository: CourseRepository by lazy {
        CourseRepositoryImpl(
            LocalCourseDataSourceImpl(AppDatabase.getInstance(applicationContext).courseDao()),
            RemoteCourseDataSourceImpl(retrofit.create(CourseService::class.java))
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