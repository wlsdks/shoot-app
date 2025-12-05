package com.shoot.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatRoom(
    val roomId: Long,
    val title: String,
    val description: String? = null,
    val type: ChatRoomType,
    val participants: List<User>,
    val lastMessage: String? = null,
    val lastMessageAt: String? = null,
    val unreadCount: Int = 0,
    val isFavorite: Boolean = false,
    val isNotificationEnabled: Boolean = true,
    val createdAt: String
)

@Serializable
enum class ChatRoomType {
    DIRECT,
    GROUP
}
