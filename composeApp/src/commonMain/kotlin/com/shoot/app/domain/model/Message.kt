package com.shoot.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: String,
    val roomId: Long,
    val senderId: Long,
    val content: MessageContent,
    val status: MessageStatus,
    val threadId: String? = null,
    val replyToMessageId: String? = null,
    val reactions: Map<String, List<Long>> = emptyMap(),
    val mentions: List<Long> = emptyList(),
    val createdAt: String,
    val updatedAt: String? = null,
    val tempId: String? = null // 로컬에서 생성한 임시 ID
)

@Serializable
data class MessageContent(
    val text: String,
    val type: MessageType,
    val attachments: List<Attachment> = emptyList(),
    val isEdited: Boolean = false,
    val isDeleted: Boolean = false,
    val urlPreview: UrlPreview? = null
)

@Serializable
enum class MessageType {
    TEXT,
    IMAGE,
    VIDEO,
    AUDIO,
    FILE,
    VOICE
}

@Serializable
enum class MessageStatus {
    SENDING,      // 클라이언트가 전송 중
    SENT_TO_KAFKA, // Kafka에 전송됨
    PROCESSING,   // 서버에서 처리 중
    SAVED,        // DB에 저장 완료
    FAILED        // 전송 실패
}

@Serializable
data class Attachment(
    val id: String,
    val filename: String,
    val contentType: String,
    val size: Long,
    val url: String,
    val thumbnailUrl: String? = null
)

@Serializable
data class UrlPreview(
    val url: String,
    val title: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val siteName: String? = null
)

@Serializable
enum class ReactionType {
    LIKE,    // 👍
    LOVE,    // ❤️
    HAHA,    // 😄
    WOW,     // 😮
    SAD,     // 😢
    ANGRY    // 😡
}

@Serializable
data class MessageReaction(
    val type: ReactionType,
    val userIds: List<Long>
)
