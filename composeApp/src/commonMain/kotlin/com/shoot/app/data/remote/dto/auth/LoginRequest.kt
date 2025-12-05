package com.shoot.app.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val userId: Long,
    val username: String
)

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)
