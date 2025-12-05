package com.shoot.app.data.remote.dto.chatroom

import com.shoot.app.data.remote.dto.user.UserResponse
import com.shoot.app.data.remote.dto.user.toDomain
import com.shoot.app.domain.model.ChatRoom
import com.shoot.app.domain.model.ChatRoomType
import kotlinx.serialization.Serializable

@Serializable
data class ChatRoomResponse(
    val roomId: Long,
    val title: String,
    val description: String? = null,
    val type: String,
    val participants: List<UserResponse>,
    val lastMessage: String? = null,
    val lastMessageAt: String? = null,
    val unreadCount: Int = 0,
    val isFavorite: Boolean = false,
    val isNotificationEnabled: Boolean = true,
    val createdAt: String
)

fun ChatRoomResponse.toDomain(): ChatRoom {
    return ChatRoom(
        roomId = roomId,
        title = title,
        description = description,
        type = when (type.uppercase()) {
            "DIRECT" -> ChatRoomType.DIRECT
            "GROUP" -> ChatRoomType.GROUP
            else -> ChatRoomType.DIRECT
        },
        participants = participants.map { it.toDomain() },
        lastMessage = lastMessage,
        lastMessageAt = lastMessageAt,
        unreadCount = unreadCount,
        isFavorite = isFavorite,
        isNotificationEnabled = isNotificationEnabled,
        createdAt = createdAt
    )
}

@Serializable
data class CreateDirectChatRequest(
    val senderId: Long,
    val recipientId: Long
)

@Serializable
data class CreateGroupChatRequest(
    val creatorId: Long,
    val participantIds: List<Long>,
    val title: String,
    val description: String? = null
)

@Serializable
data class ToggleFavoriteRequest(
    val userId: Long,
    val roomId: Long,
    val isFavorite: Boolean
)
