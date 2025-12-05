package com.shoot.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId: Long,
    val username: String,
    val nickname: String,
    val email: String? = null,
    val bio: String? = null,
    val profileImageUrl: String? = null,
    val backgroundImageUrl: String? = null,
    val userCode: String? = null,
    val status: UserStatus = UserStatus.OFFLINE,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
enum class UserStatus {
    ONLINE,
    OFFLINE,
    AWAY
}
