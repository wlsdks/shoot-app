package com.shoot.app.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val username: String,
    val nickname: String,
    val password: String,
    val email: String,
    val bio: String? = null
)
