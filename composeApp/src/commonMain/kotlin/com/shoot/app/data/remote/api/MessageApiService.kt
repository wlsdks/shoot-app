package com.shoot.app.data.remote.api

import com.shoot.app.data.remote.dto.common.ResponseDto
import com.shoot.app.data.remote.dto.message.MessageResponse
import com.shoot.app.data.remote.dto.message.MessagesResponse
import com.shoot.app.data.remote.dto.message.SendMessageRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class MessageApiService(private val client: HttpClient) {

    /**
     * 채팅방의 메시지 목록 조회 (페이지네이션)
     */
    suspend fun getMessages(
        roomId: Long,
        page: Int = 0,
        size: Int = 50,
        cursor: String? = null
    ): ResponseDto<MessagesResponse> {
        return client.get("/chat-rooms/$roomId/messages") {
            parameter("page", page)
            parameter("size", size)
            cursor?.let { parameter("cursor", it) }
        }.body()
    }

    /**
     * 메시지 전송 (REST API - fallback용)
     * 일반적으로는 WebSocket을 사용하지만, WebSocket 연결이 불가능할 때 사용
     */
    suspend fun sendMessage(request: SendMessageRequest): ResponseDto<MessageResponse> {
        return client.post("/messages") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /**
     * 특정 메시지 조회
     */
    suspend fun getMessage(messageId: String): ResponseDto<MessageResponse> {
        return client.get("/messages/$messageId").body()
    }

    /**
     * 메시지 편집
     */
    suspend fun editMessage(messageId: String, newContent: String, userId: Long): ResponseDto<MessageResponse> {
        return client.put("/messages/edit") {
            contentType(ContentType.Application.Json)
            setBody(
                mapOf(
                    "messageId" to messageId,
                    "newContent" to newContent,
                    "userId" to userId
                )
            )
        }.body()
    }

    /**
     * 메시지 삭제
     */
    suspend fun deleteMessage(messageId: String, userId: Long): ResponseDto<MessageResponse> {
        return client.delete("/messages/delete") {
            contentType(ContentType.Application.Json)
            setBody(
                mapOf(
                    "messageId" to messageId,
                    "userId" to userId
                )
            )
        }.body()
    }

    /**
     * 메시지 읽음 표시
     */
    suspend fun markAsRead(roomId: Long, userId: Long, messageIds: List<String>): ResponseDto<Unit> {
        return client.post("/messages/read") {
            contentType(ContentType.Application.Json)
            setBody(
                mapOf(
                    "roomId" to roomId,
                    "userId" to userId,
                    "messageIds" to messageIds
                )
            )
        }.body()
    }

    /**
     * 채팅방의 안 읽은 메시지 수 조회
     */
    suspend fun getUnreadCount(roomId: Long, userId: Long): ResponseDto<Int> {
        return client.get("/chat-rooms/$roomId/unread-count") {
            parameter("userId", userId)
        }.body()
    }

    /**
     * 메시지 검색
     */
    suspend fun searchMessages(
        roomId: Long,
        query: String,
        page: Int = 0,
        size: Int = 20
    ): ResponseDto<MessagesResponse> {
        return client.get("/chat-rooms/$roomId/messages/search") {
            parameter("query", query)
            parameter("page", page)
            parameter("size", size)
        }.body()
    }
}
