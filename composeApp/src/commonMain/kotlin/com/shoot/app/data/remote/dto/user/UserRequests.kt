package com.shoot.app.data.remote.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequest(
    val nickname: String? = null,
    val bio: String? = null,
    val profileImageUrl: String? = null,
    val backgroundImageUrl: String? = null
)

@Serializable
data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)

@Serializable
data class UpdateStatusRequest(
    val status: String
)
