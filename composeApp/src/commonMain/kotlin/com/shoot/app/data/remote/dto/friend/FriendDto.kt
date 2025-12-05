package com.shoot.app.data.remote.dto.friend

import com.shoot.app.domain.model.Friend
import com.shoot.app.domain.model.FriendRequest
import com.shoot.app.domain.model.FriendRequestStatus
import com.shoot.app.domain.model.UserStatus
import kotlinx.serialization.Serializable

@Serializable
data class FriendResponse(
    val userId: Long,
    val username: String,
    val nickname: String,
    val profileImageUrl: String? = null,
    val status: String? = null,
    val isFriend: Boolean = true
)

@Serializable
data class FriendRequestResponse(
    val requestId: String,
    val senderId: Long,
    val recipientId: Long,
    val senderUsername: String? = null,
    val senderNickname: String? = null,
    val senderProfileImageUrl: String? = null,
    val status: String,
    val message: String? = null,
    val createdAt: String
)

@Serializable
data class SendFriendRequestDto(
    val senderId: Long,
    val recipientId: Long
)

@Serializable
data class AcceptFriendRequestDto(
    val requestId: String,
    val responderId: Long
)

@Serializable
data class RejectFriendRequestDto(
    val requestId: String,
    val responderId: Long
)

// Mapper functions
fun FriendResponse.toDomain(): Friend {
    return Friend(
        userId = userId,
        username = username,
        nickname = nickname,
        profileImageUrl = profileImageUrl,
        status = when (status?.uppercase()) {
            "ONLINE" -> UserStatus.ONLINE
            "AWAY" -> UserStatus.AWAY
            else -> UserStatus.OFFLINE
        },
        isFriend = isFriend
    )
}

fun FriendRequestResponse.toDomain(): FriendRequest {
    return FriendRequest(
        requestId = requestId,
        senderId = senderId,
        recipientId = recipientId,
        senderUsername = senderUsername ?: "",
        senderNickname = senderNickname ?: "",
        senderProfileImageUrl = senderProfileImageUrl,
        status = when (status.uppercase()) {
            "PENDING" -> FriendRequestStatus.PENDING
            "ACCEPTED" -> FriendRequestStatus.ACCEPTED
            "REJECTED" -> FriendRequestStatus.REJECTED
            "CANCELLED" -> FriendRequestStatus.CANCELLED
            else -> FriendRequestStatus.PENDING
        },
        message = message,
        createdAt = createdAt
    )
}
