package com.shoot.app.data.repository

import com.shoot.app.data.remote.api.ChatRoomApiService
import com.shoot.app.data.remote.dto.chatroom.toDomain
import com.shoot.app.domain.model.ChatRoom
import com.shoot.app.utils.safeApiCall

interface ChatRoomRepository {
    suspend fun getChatRooms(userId: Long): Result<List<ChatRoom>>
    suspend fun createDirectChat(senderId: Long, recipientId: Long): Result<ChatRoom>
    suspend fun createGroupChat(
        creatorId: Long,
        participantIds: List<Long>,
        title: String,
        description: String? = null
    ): Result<ChatRoom>
    suspend fun exitChatRoom(roomId: Long, userId: Long): Result<Unit>
    suspend fun toggleFavorite(userId: Long, roomId: Long, isFavorite: Boolean): Result<ChatRoom>
    suspend fun searchChatRooms(userId: Long, query: String): Result<List<ChatRoom>>
}

class ChatRoomRepositoryImpl(
    private val chatRoomApiService: ChatRoomApiService
) : ChatRoomRepository {

    override suspend fun getChatRooms(userId: Long): Result<List<ChatRoom>> {
        return safeApiCall {
            val response = chatRoomApiService.getChatRooms(userId)

            if (response.success && response.data != null) {
                response.data.map { it.toDomain() }
            } else {
                throw Exception(response.message ?: "Failed to get chat rooms")
            }
        }
    }

    override suspend fun createDirectChat(senderId: Long, recipientId: Long): Result<ChatRoom> {
        return safeApiCall {
            val response = chatRoomApiService.createDirectChat(senderId, recipientId)

            if (response.success && response.data != null) {
                response.data.toDomain()
            } else {
                throw Exception(response.message ?: "Failed to create direct chat")
            }
        }
    }

    override suspend fun createGroupChat(
        creatorId: Long,
        participantIds: List<Long>,
        title: String,
        description: String?
    ): Result<ChatRoom> {
        return safeApiCall {
            val response = chatRoomApiService.createGroupChat(creatorId, participantIds, title, description)

            if (response.success && response.data != null) {
                response.data.toDomain()
            } else {
                throw Exception(response.message ?: "Failed to create group chat")
            }
        }
    }

    override suspend fun exitChatRoom(roomId: Long, userId: Long): Result<Unit> {
        return safeApiCall {
            val response = chatRoomApiService.exitChatRoom(roomId, userId)

            if (response.success) {
                Unit
            } else {
                throw Exception(response.message ?: "Failed to exit chat room")
            }
        }
    }

    override suspend fun toggleFavorite(userId: Long, roomId: Long, isFavorite: Boolean): Result<ChatRoom> {
        return safeApiCall {
            val response = chatRoomApiService.toggleFavorite(userId, roomId, isFavorite)

            if (response.success && response.data != null) {
                response.data.toDomain()
            } else {
                throw Exception(response.message ?: "Failed to toggle favorite")
            }
        }
    }

    override suspend fun searchChatRooms(userId: Long, query: String): Result<List<ChatRoom>> {
        return safeApiCall {
            val response = chatRoomApiService.searchChatRooms(userId, query)

            if (response.success && response.data != null) {
                response.data.map { it.toDomain() }
            } else {
                throw Exception(response.message ?: "Failed to search chat rooms")
            }
        }
    }
}
