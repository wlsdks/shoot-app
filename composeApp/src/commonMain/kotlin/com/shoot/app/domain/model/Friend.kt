package com.shoot.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Friend(
    val userId: Long,
    val username: String,
    val nickname: String,
    val profileImageUrl: String? = null,
    val status: UserStatus = UserStatus.OFFLINE,
    val isFriend: Boolean = true
)

@Serializable
data class FriendRequest(
    val requestId: String,
    val senderId: Long,
    val recipientId: Long,
    val senderUsername: String,
    val senderNickname: String,
    val senderProfileImageUrl: String? = null,
    val status: FriendRequestStatus,
    val message: String? = null,
    val createdAt: String
)

@Serializable
enum class FriendRequestStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    CANCELLED
}
