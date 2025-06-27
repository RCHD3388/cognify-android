package com.cognifyteam.cognifyapp.data.repositories.auth

import android.util.Log
import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.data.models.UserJson
import com.cognifyteam.cognifyapp.data.sources.local.datasources.LocalAuthDataSource
import com.cognifyteam.cognifyapp.data.sources.remote.auth.RemoteAuthDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

interface AuthRepository {
    val loggedInUser: Flow<User?>
    suspend fun register(firebaseId: String, name: String, email: String, role: String): Result<User>
    suspend fun login(firebaseId: String): Result<User>
    suspend fun logout()
}

class AuthRepositoryImpl(
    private val localAuthDataSource: LocalAuthDataSource,
    private val remoteAuthDataSource: RemoteAuthDataSource
): AuthRepository {
    private val _loggedInUser = MutableStateFlow<User?>(null)

    // 2. Expose sebagai Flow yang tidak bisa diubah dari luar.
    override val loggedInUser: Flow<User?> = _loggedInUser.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            _loggedInUser.value = localAuthDataSource.getCurrentUser()
        }
    }

    override suspend fun register(firebaseId: String, name: String, email: String, role: String): Result<User> {
        return remoteAuthDataSource.register(UserJson(firebaseId, name, email, role))
    }

    override suspend fun login(firebaseId: String): Result<User> {
        val result = remoteAuthDataSource.login(firebaseId)
        result.onSuccess { user ->
            localAuthDataSource.insertOrReplace(user)
            _loggedInUser.value = user
        }
        return result
    }

    override suspend fun logout() {
        localAuthDataSource.clearAll() // Hapus dari database lokal
        _loggedInUser.value = null // Kosongkan state
    }
}