package com.shoot.app.data.remote.api

import com.shoot.app.data.remote.dto.chatroom.ChatRoomResponse
import com.shoot.app.data.remote.dto.chatroom.CreateDirectChatRequest
import com.shoot.app.data.remote.dto.chatroom.CreateGroupChatRequest
import com.shoot.app.data.remote.dto.chatroom.ToggleFavoriteRequest
import com.shoot.app.data.remote.dto.common.ResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ChatRoomApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String = "http://localhost:8100/api/v1"
) {

    suspend fun getChatRooms(userId: Long): ResponseDto<List<ChatRoomResponse>> {
        return httpClient.get("$baseUrl/chatrooms") {
            parameter("userId", userId)
        }.body()
    }

    suspend fun createDirectChat(
        senderId: Long,
        recipientId: Long
    ): ResponseDto<ChatRoomResponse> {
        return httpClient.post("$baseUrl/chatrooms/create/direct") {
            contentType(ContentType.Application.Json)
            setBody(CreateDirectChatRequest(senderId, recipientId))
        }.body()
    }

    suspend fun createGroupChat(
        creatorId: Long,
        participantIds: List<Long>,
        title: String,
        description: String? = null
    ): ResponseDto<ChatRoomResponse> {
        return httpClient.post("$baseUrl/chatrooms/group") {
            contentType(ContentType.Application.Json)
            setBody(CreateGroupChatRequest(creatorId, participantIds, title, description))
        }.body()
    }

    suspend fun exitChatRoom(roomId: Long, userId: Long): ResponseDto<Unit> {
        return httpClient.delete("$baseUrl/chatrooms/$roomId/exit") {
            parameter("userId", userId)
        }.body()
    }

    suspend fun toggleFavorite(
        userId: Long,
        roomId: Long,
        isFavorite: Boolean
    ): ResponseDto<ChatRoomResponse> {
        return httpClient.post("$baseUrl/chatrooms/favorite") {
            contentType(ContentType.Application.Json)
            setBody(ToggleFavoriteRequest(userId, roomId, isFavorite))
        }.body()
    }

    suspend fun searchChatRooms(
        userId: Long,
        query: String
    ): ResponseDto<List<ChatRoomResponse>> {
        return httpClient.get("$baseUrl/chatrooms/search") {
            parameter("userId", userId)
            parameter("query", query)
        }.body()
    }
}
