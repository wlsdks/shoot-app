package com.shoot.app.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface TokenManager {
    suspend fun saveAccessToken(token: String)
    suspend fun saveRefreshToken(token: String)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun clearTokens()
    fun isLoggedIn(): Flow<Boolean>
}

class TokenManagerImpl : TokenManager {

    // In-memory storage for now (will be replaced with persistent storage)
    private var accessToken: String? = null
    private var refreshToken: String? = null

    private val _isLoggedIn = MutableStateFlow(false)

    override suspend fun saveAccessToken(token: String) {
        accessToken = token
        _isLoggedIn.value = true
    }

    override suspend fun saveRefreshToken(token: String) {
        refreshToken = token
    }

    override suspend fun getAccessToken(): String? {
        return accessToken
    }

    override suspend fun getRefreshToken(): String? {
        return refreshToken
    }

    override suspend fun clearTokens() {
        accessToken = null
        refreshToken = null
        _isLoggedIn.value = false
    }

    override fun isLoggedIn(): Flow<Boolean> {
        return _isLoggedIn.asStateFlow()
    }
}
