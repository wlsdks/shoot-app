package com.shoot.app.data.repository

import com.shoot.app.data.remote.api.MessageApiService
import com.shoot.app.data.remote.dto.message.SendMessageRequest
import com.shoot.app.data.remote.dto.message.toDomain
import com.shoot.app.domain.model.Message
import com.shoot.app.utils.safeApiCall

interface MessageRepository {
    suspend fun getMessages(
        roomId: Long,
        page: Int = 0,
        size: Int = 50,
        cursor: String? = null
    ): Result<MessagesResult>

    suspend fun sendMessage(request: SendMessageRequest): Result<Message>
    suspend fun getMessage(messageId: String): Result<Message>
    suspend fun editMessage(messageId: String, newContent: String, userId: Long): Result<Message>
    suspend fun deleteMessage(messageId: String, userId: Long): Result<Message>
    suspend fun markAsRead(roomId: Long, userId: Long, messageIds: List<String>): Result<Unit>
    suspend fun getUnreadCount(roomId: Long, userId: Long): Result<Int>
    suspend fun searchMessages(
        roomId: Long,
        query: String,
        page: Int = 0,
        size: Int = 20
    ): Result<MessagesResult>
}

data class MessagesResult(
    val messages: List<Message>,
    val hasMore: Boolean,
    val nextCursor: String? = null
)

class MessageRepositoryImpl(
    private val messageApiService: MessageApiService
) : MessageRepository {

    override suspend fun getMessages(
        roomId: Long,
        page: Int,
        size: Int,
        cursor: String?
    ): Result<MessagesResult> {
        return safeApiCall {
            val response = messageApiService.getMessages(roomId, page, size, cursor)

            if (response.success && response.data != null) {
                MessagesResult(
                    messages = response.data.messages.map { it.toDomain() },
                    hasMore = response.data.hasMore,
                    nextCursor = response.data.nextCursor
                )
            } else {
                throw Exception(response.message ?: "Failed to get messages")
            }
        }
    }

    override suspend fun sendMessage(request: SendMessageRequest): Result<Message> {
        return safeApiCall {
            val response = messageApiService.sendMessage(request)

            if (response.success && response.data != null) {
                response.data.toDomain(tempId = request.tempId)
            } else {
                throw Exception(response.message ?: "Failed to send message")
            }
        }
    }

    override suspend fun getMessage(messageId: String): Result<Message> {
        return safeApiCall {
            val response = messageApiService.getMessage(messageId)

            if (response.success && response.data != null) {
                response.data.toDomain()
            } else {
                throw Exception(response.message ?: "Failed to get message")
            }
        }
    }

    override suspend fun editMessage(
        messageId: String,
        newContent: String,
        userId: Long
    ): Result<Message> {
        return safeApiCall {
            val response = messageApiService.editMessage(messageId, newContent, userId)

            if (response.success && response.data != null) {
                response.data.toDomain()
            } else {
                throw Exception(response.message ?: "Failed to edit message")
            }
        }
    }

    override suspend fun deleteMessage(messageId: String, userId: Long): Result<Message> {
        return safeApiCall {
            val response = messageApiService.deleteMessage(messageId, userId)

            if (response.success && response.data != null) {
                response.data.toDomain()
            } else {
                throw Exception(response.message ?: "Failed to delete message")
            }
        }
    }

    override suspend fun markAsRead(
        roomId: Long,
        userId: Long,
        messageIds: List<String>
    ): Result<Unit> {
        return safeApiCall {
            val response = messageApiService.markAsRead(roomId, userId, messageIds)

            if (response.success) {
                Unit
            } else {
                throw Exception(response.message ?: "Failed to mark messages as read")
            }
        }
    }

    override suspend fun getUnreadCount(roomId: Long, userId: Long): Result<Int> {
        return safeApiCall {
            val response = messageApiService.getUnreadCount(roomId, userId)

            if (response.success && response.data != null) {
                response.data
            } else {
                throw Exception(response.message ?: "Failed to get unread count")
            }
        }
    }

    override suspend fun searchMessages(
        roomId: Long,
        query: String,
        page: Int,
        size: Int
    ): Result<MessagesResult> {
        return safeApiCall {
            val response = messageApiService.searchMessages(roomId, query, page, size)

            if (response.success && response.data != null) {
                MessagesResult(
                    messages = response.data.messages.map { it.toDomain() },
                    hasMore = response.data.hasMore,
                    nextCursor = response.data.nextCursor
                )
            } else {
                throw Exception(response.message ?: "Failed to search messages")
            }
        }
    }
}
