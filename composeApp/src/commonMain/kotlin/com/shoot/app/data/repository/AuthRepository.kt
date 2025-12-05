package com.shoot.app.data.repository

import com.shoot.app.data.remote.api.AuthApiService
import com.shoot.app.data.remote.dto.auth.LoginRequest
import com.shoot.app.data.remote.dto.auth.RegisterRequest
import com.shoot.app.domain.model.User
import com.shoot.app.data.remote.dto.user.toDomain
import com.shoot.app.utils.safeApiCall

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<LoginData>
    suspend fun register(
        username: String,
        nickname: String,
        password: String,
        email: String,
        bio: String?
    ): Result<User>
    suspend fun refreshToken(refreshToken: String): Result<LoginData>
    suspend fun getCurrentUser(): Result<User>
    suspend fun logout(): Result<Unit>
}

data class LoginData(
    val accessToken: String,
    val refreshToken: String,
    val userId: Long,
    val username: String
)

class AuthRepositoryImpl(
    private val authApiService: AuthApiService
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<LoginData> {
        return safeApiCall {
            val response = authApiService.login(LoginRequest(username, password))

            if (response.success && response.data != null) {
                LoginData(
                    accessToken = response.data.accessToken,
                    refreshToken = response.data.refreshToken,
                    userId = response.data.userId,
                    username = response.data.username
                )
            } else {
                throw Exception(response.message ?: "Login failed")
            }
        }
    }

    override suspend fun register(
        username: String,
        nickname: String,
        password: String,
        email: String,
        bio: String?
    ): Result<User> {
        return safeApiCall {
            val response = authApiService.register(
                RegisterRequest(
                    username = username,
                    nickname = nickname,
                    password = password,
                    email = email,
                    bio = bio
                )
            )

            if (response.success && response.data != null) {
                response.data.toDomain()
            } else {
                throw Exception(response.message ?: "Registration failed")
            }
        }
    }

    override suspend fun refreshToken(refreshToken: String): Result<LoginData> {
        return safeApiCall {
            val response = authApiService.refreshToken(
                com.shoot.app.data.remote.dto.auth.RefreshTokenRequest(refreshToken)
            )

            if (response.success && response.data != null) {
                LoginData(
                    accessToken = response.data.accessToken,
                    refreshToken = response.data.refreshToken,
                    userId = response.data.userId,
                    username = response.data.username
                )
            } else {
                throw Exception(response.message ?: "Token refresh failed")
            }
        }
    }

    override suspend fun getCurrentUser(): Result<User> {
        return safeApiCall {
            val response = authApiService.getCurrentUser()

            if (response.success && response.data != null) {
                response.data.toDomain()
            } else {
                throw Exception(response.message ?: "Failed to get current user")
            }
        }
    }

    override suspend fun logout(): Result<Unit> {
        return safeApiCall {
            authApiService.logout()
        }
    }
}
