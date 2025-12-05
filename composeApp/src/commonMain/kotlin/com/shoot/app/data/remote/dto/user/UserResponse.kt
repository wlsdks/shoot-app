package com.shoot.app.data.remote.dto.user

import com.shoot.app.domain.model.User
import com.shoot.app.domain.model.UserStatus
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val userId: Long,
    val username: String,
    val nickname: String,
    val email: String? = null,
    val bio: String? = null,
    val profileImageUrl: String? = null,
    val backgroundImageUrl: String? = null,
    val userCode: String? = null,
    val status: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

fun UserResponse.toDomain(): User {
    return User(
        userId = userId,
        username = username,
        nickname = nickname,
        email = email,
        bio = bio,
        profileImageUrl = profileImageUrl,
        backgroundImageUrl = backgroundImageUrl,
        userCode = userCode,
        status = when (status?.uppercase()) {
            "ONLINE" -> UserStatus.ONLINE
            "AWAY" -> UserStatus.AWAY
            else -> UserStatus.OFFLINE
        },
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
