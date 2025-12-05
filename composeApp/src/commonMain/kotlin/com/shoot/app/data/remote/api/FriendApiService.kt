package com.shoot.app.data.remote.api

import com.shoot.app.data.remote.dto.common.ResponseDto
import com.shoot.app.data.remote.dto.friend.AcceptFriendRequestDto
import com.shoot.app.data.remote.dto.friend.FriendRequestResponse
import com.shoot.app.data.remote.dto.friend.FriendResponse
import com.shoot.app.data.remote.dto.friend.RejectFriendRequestDto
import com.shoot.app.data.remote.dto.friend.SendFriendRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class FriendApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String = "http://localhost:8100/api/v1"
) {

    suspend fun getFriends(userId: Long): ResponseDto<List<FriendResponse>> {
        return httpClient.get("$baseUrl/friends") {
            parameter("userId", userId)
        }.body()
    }

    suspend fun getIncomingRequests(userId: Long): ResponseDto<List<FriendRequestResponse>> {
        return httpClient.get("$baseUrl/friends/incoming") {
            parameter("userId", userId)
        }.body()
    }

    suspend fun getOutgoingRequests(userId: Long): ResponseDto<List<FriendRequestResponse>> {
        return httpClient.get("$baseUrl/friends/outgoing") {
            parameter("userId", userId)
        }.body()
    }

    suspend fun sendFriendRequest(senderId: Long, recipientId: Long): ResponseDto<FriendRequestResponse> {
        return httpClient.post("$baseUrl/friends/request") {
            contentType(ContentType.Application.Json)
            setBody(SendFriendRequestDto(senderId, recipientId))
        }.body()
    }

    suspend fun acceptFriendRequest(requestId: String, responderId: Long): ResponseDto<FriendRequestResponse> {
        return httpClient.post("$baseUrl/friends/accept") {
            contentType(ContentType.Application.Json)
            setBody(AcceptFriendRequestDto(requestId, responderId))
        }.body()
    }

    suspend fun rejectFriendRequest(requestId: String, responderId: Long): ResponseDto<FriendRequestResponse> {
        return httpClient.post("$baseUrl/friends/reject") {
            contentType(ContentType.Application.Json)
            setBody(RejectFriendRequestDto(requestId, responderId))
        }.body()
    }

    suspend fun cancelFriendRequest(requestId: String, senderId: Long): ResponseDto<Unit> {
        return httpClient.delete("$baseUrl/friends/request/$requestId") {
            parameter("senderId", senderId)
        }.body()
    }

    suspend fun removeFriend(userId: Long, friendId: Long): ResponseDto<Unit> {
        return httpClient.delete("$baseUrl/friends/me/friends/$friendId") {
            parameter("userId", userId)
        }.body()
    }

    suspend fun searchFriends(userId: Long, query: String): ResponseDto<List<FriendResponse>> {
        return httpClient.get("$baseUrl/friends/search") {
            parameter("userId", userId)
            parameter("query", query)
        }.body()
    }
}
