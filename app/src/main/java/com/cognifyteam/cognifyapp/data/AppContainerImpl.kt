package com.cognifyteam.cognifyapp.data

import android.content.Context
import android.net.ConnectivityManager
import com.cognifyteam.cognifyapp.data.repositories.CourseRepository
import com.cognifyteam.cognifyapp.data.repositories.CourseRepositoryImpl
import com.cognifyteam.cognifyapp.data.repositories.DiscussionRepository
import com.cognifyteam.cognifyapp.data.repositories.DiscussionRepositoryImpl
import com.cognifyteam.cognifyapp.data.repositories.FollowRepository
import com.cognifyteam.cognifyapp.data.repositories.FollowRepositoryImpl
import com.cognifyteam.cognifyapp.data.repositories.TransactionRepository
import com.cognifyteam.cognifyapp.data.repositories.TransactionRepositoryImpl
import com.cognifyteam.cognifyapp.data.repositories.auth.AuthRepository
import com.cognifyteam.cognifyapp.data.repositories.auth.AuthRepositoryImpl
import com.cognifyteam.cognifyapp.data.repositories.profile.ProfileRepository
import com.cognifyteam.cognifyapp.data.repositories.profile.ProfileRepositoryImpl
import com.cognifyteam.cognifyapp.data.repositories.smart.SmartRepository
import com.cognifyteam.cognifyapp.data.repositories.smart.SmartRepositoryImpl
import com.cognifyteam.cognifyapp.data.sources.local.AppDatabase
import com.cognifyteam.cognifyapp.data.sources.local.datasources.LocalAuthDataSource
import com.cognifyteam.cognifyapp.data.sources.local.datasources.LocalAuthDataSourceImpl
import com.cognifyteam.cognifyapp.data.sources.local.datasources.LocalCourseDataSourceImpl
import com.cognifyteam.cognifyapp.data.sources.local.datasources.LocalDiscussionDataSourceImpl
import com.cognifyteam.cognifyapp.data.sources.local.datasources.LocalFollowDataSourceImpl
import com.cognifyteam.cognifyapp.data.sources.local.datasources.LocalProfileDataSourceImpl
import com.cognifyteam.cognifyapp.data.sources.local.datasources.LocalSmartDataSourceImpl
import com.cognifyteam.cognifyapp.data.sources.local.datasources.LocalTransactionDataSourceImpl
import com.cognifyteam.cognifyapp.data.sources.remote.ErrorResponse
import com.cognifyteam.cognifyapp.data.sources.remote.auth.RemoteAuthDataSourceImpl
import com.cognifyteam.cognifyapp.data.sources.remote.course.RemoteCourseDataSourceImpl
import com.cognifyteam.cognifyapp.data.sources.remote.course.RemoteDiscussionDataSourceImpl
import com.cognifyteam.cognifyapp.data.sources.remote.profile.RemoteProfileDataSourceImpl
import com.cognifyteam.cognifyapp.data.sources.remote.services.AuthService
import com.cognifyteam.cognifyapp.data.sources.remote.services.CourseService
import com.cognifyteam.cognifyapp.data.sources.remote.services.DiscussionService
import com.cognifyteam.cognifyapp.data.sources.remote.services.FollowService
import com.cognifyteam.cognifyapp.data.sources.remote.services.MaterialService
import com.cognifyteam.cognifyapp.data.sources.remote.services.ProfileService
import com.cognifyteam.cognifyapp.data.sources.remote.services.SmartService
import com.cognifyteam.cognifyapp.data.sources.remote.smart.RemoteSmartDataSourceImpl
import com.cognifyteam.cognifyapp.data.sources.remote.services.SectionService
import com.cognifyteam.cognifyapp.data.sources.remote.services.TransactionService
import com.cognifyteam.cognifyapp.data.sources.remote.transaction.RemoteTransactionDataSourceImpl
import com.cognifyteam.cognifyapp.data.sources.remote.users.RemoteFollowDataSourceImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

interface AppContainer{
    val authRepository: AuthRepository
    val smartRepository: SmartRepository
    val profileRepository: ProfileRepository
    val courseRepository: CourseRepository
    val discussionRepository: DiscussionRepository
    val followRepository: FollowRepository
    val transactionRepository: TransactionRepository
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

    override val smartRepository: SmartRepository by lazy {
        SmartRepositoryImpl(
            LocalSmartDataSourceImpl(AppDatabase.getInstance(applicationContext)),
            RemoteSmartDataSourceImpl(retrofit.create(SmartService::class.java))
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
            RemoteCourseDataSourceImpl(retrofit.create(CourseService::class.java),retrofit.create(SectionService::class.java),retrofit.create(MaterialService::class.java),moshi),
            applicationContext


        )
    }

    override val discussionRepository: DiscussionRepository by lazy {
        DiscussionRepositoryImpl(
            LocalDiscussionDataSourceImpl(
                AppDatabase.getInstance(applicationContext).discussionDao()
            ),
            RemoteDiscussionDataSourceImpl(retrofit.create(DiscussionService::class.java))
        )
    }

    override val followRepository: FollowRepository by lazy {
        FollowRepositoryImpl(
            LocalFollowDataSourceImpl(
                AppDatabase.getInstance(applicationContext).followDao()
            ),
            RemoteFollowDataSourceImpl(retrofit.create(FollowService::class.java))
        )
    }

    override val transactionRepository: TransactionRepository by lazy {
        TransactionRepositoryImpl(
            LocalTransactionDataSourceImpl(
                AppDatabase.getInstance(applicationContext).transactionDao()
            ),
            RemoteTransactionDataSourceImpl(retrofit.create(TransactionService::class.java))
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