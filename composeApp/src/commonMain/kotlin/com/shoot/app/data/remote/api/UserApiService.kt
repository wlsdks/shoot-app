package com.shoot.app.data.remote.api

import com.shoot.app.data.remote.dto.common.ResponseDto
import com.shoot.app.data.remote.dto.user.ChangePasswordRequest
import com.shoot.app.data.remote.dto.user.UpdateProfileRequest
import com.shoot.app.data.remote.dto.user.UpdateStatusRequest
import com.shoot.app.data.remote.dto.user.UserResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class UserApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String = "http://localhost:8100/api/v1"
) {

    suspend fun updateProfile(request: UpdateProfileRequest): ResponseDto<UserResponse> {
        return httpClient.patch("$baseUrl/users/me") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun changePassword(request: ChangePasswordRequest): ResponseDto<Unit> {
        return httpClient.put("$baseUrl/users/me/password") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun updateStatus(request: UpdateStatusRequest): ResponseDto<UserResponse> {
        return httpClient.patch("$baseUrl/users/me/status") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun deleteAccount(): ResponseDto<Unit> {
        return httpClient.delete("$baseUrl/users/me").body()
    }

    suspend fun getUserById(userId: Long): ResponseDto<UserResponse> {
        return httpClient.get("$baseUrl/users/$userId").body()
    }

    suspend fun getUserByUsername(username: String): ResponseDto<UserResponse> {
        return httpClient.get("$baseUrl/users/username/$username").body()
    }

    suspend fun searchUsers(query: String): ResponseDto<List<UserResponse>> {
        return httpClient.get("$baseUrl/users/search?q=$query").body()
    }
}
