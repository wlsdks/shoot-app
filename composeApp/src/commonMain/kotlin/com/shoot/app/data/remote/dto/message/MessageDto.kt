package com.shoot.app.data.remote.dto.message

import com.shoot.app.domain.model.*
import kotlinx.serialization.Serializable

// 서버로부터 받는 메시지 응답 DTO
@Serializable
data class MessageResponse(
    val id: String,
    val roomId: Long,
    val senderId: Long,
    val content: MessageContentResponse,
    val status: String,
    val threadId: String? = null,
    val replyToMessageId: String? = null,
    val reactions: Map<String, List<Long>> = emptyMap(),
    val mentions: List<Long> = emptyList(),
    val createdAt: String,
    val updatedAt: String? = null
)

@Serializable
data class MessageContentResponse(
    val text: String,
    val type: String,
    val attachments: List<AttachmentResponse> = emptyList(),
    val isEdited: Boolean = false,
    val isDeleted: Boolean = false,
    val urlPreview: UrlPreviewResponse? = null
)

@Serializable
data class AttachmentResponse(
    val id: String,
    val filename: String,
    val contentType: String,
    val size: Long,
    val url: String,
    val thumbnailUrl: String? = null
)

@Serializable
data class UrlPreviewResponse(
    val url: String,
    val title: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val siteName: String? = null
)

// 서버로 보내는 메시지 요청 DTO
@Serializable
data class SendMessageRequest(
    val tempId: String,
    val roomId: Long,
    val senderId: Long,
    val content: MessageContentRequest,
    val threadId: String? = null,
    val replyToMessageId: String? = null,
    val mentions: List<Long> = emptyList()
)

@Serializable
data class MessageContentRequest(
    val text: String,
    val type: String = "TEXT",
    val attachments: List<AttachmentRequest> = emptyList()
)

@Serializable
data class AttachmentRequest(
    val filename: String,
    val contentType: String,
    val size: Long,
    val url: String
)

// 메시지 목록 조회 응답
@Serializable
data class MessagesResponse(
    val messages: List<MessageResponse>,
    val hasMore: Boolean,
    val nextCursor: String? = null
)

// 메시지 상태 업데이트 (WebSocket으로 수신)
@Serializable
data class MessageStatusUpdate(
    val tempId: String? = null,
    val messageId: String,
    val status: String,
    val timestamp: String
)

// Mapper functions
fun MessageResponse.toDomain(tempId: String? = null): Message {
    return Message(
        id = id,
        roomId = roomId,
        senderId = senderId,
        content = content.toDomain(),
        status = when (status.uppercase()) {
            "SENDING" -> MessageStatus.SENDING
            "SENT_TO_KAFKA" -> MessageStatus.SENT_TO_KAFKA
            "PROCESSING" -> MessageStatus.PROCESSING
            "SAVED" -> MessageStatus.SAVED
            "FAILED" -> MessageStatus.FAILED
            else -> MessageStatus.SENDING
        },
        threadId = threadId,
        replyToMessageId = replyToMessageId,
        reactions = reactions,
        mentions = mentions,
        createdAt = createdAt,
        updatedAt = updatedAt,
        tempId = tempId
    )
}

fun MessageContentResponse.toDomain(): MessageContent {
    return MessageContent(
        text = text,
        type = when (type.uppercase()) {
            "TEXT" -> MessageType.TEXT
            "IMAGE" -> MessageType.IMAGE
            "VIDEO" -> MessageType.VIDEO
            "AUDIO" -> MessageType.AUDIO
            "FILE" -> MessageType.FILE
            "VOICE" -> MessageType.VOICE
            else -> MessageType.TEXT
        },
        attachments = attachments.map { it.toDomain() },
        isEdited = isEdited,
        isDeleted = isDeleted,
        urlPreview = urlPreview?.toDomain()
    )
}

fun AttachmentResponse.toDomain(): Attachment {
    return Attachment(
        id = id,
        filename = filename,
        contentType = contentType,
        size = size,
        url = url,
        thumbnailUrl = thumbnailUrl
    )
}

fun UrlPreviewResponse.toDomain(): UrlPreview {
    return UrlPreview(
        url = url,
        title = title,
        description = description,
        imageUrl = imageUrl,
        siteName = siteName
    )
}

// Domain to DTO
fun Message.toSendRequest(): SendMessageRequest {
    return SendMessageRequest(
        tempId = tempId ?: id,
        roomId = roomId,
        senderId = senderId,
        content = content.toRequest(),
        threadId = threadId,
        replyToMessageId = replyToMessageId,
        mentions = mentions
    )
}

fun MessageContent.toRequest(): MessageContentRequest {
    return MessageContentRequest(
        text = text,
        type = type.name,
        attachments = attachments.map { it.toRequest() }
    )
}

fun Attachment.toRequest(): AttachmentRequest {
    return AttachmentRequest(
        filename = filename,
        contentType = contentType,
        size = size,
        url = url
    )
}
