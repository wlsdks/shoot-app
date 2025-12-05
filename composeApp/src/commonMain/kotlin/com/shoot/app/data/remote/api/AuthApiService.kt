package com.shoot.app.data.remote.api

import com.shoot.app.data.remote.dto.auth.LoginRequest
import com.shoot.app.data.remote.dto.auth.LoginResponse
import com.shoot.app.data.remote.dto.auth.RefreshTokenRequest
import com.shoot.app.data.remote.dto.auth.RegisterRequest
import com.shoot.app.data.remote.dto.common.ResponseDto
import com.shoot.app.data.remote.dto.user.UserResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String = "http://localhost:8100/api/v1"
) {

    suspend fun login(request: LoginRequest): ResponseDto<LoginResponse> {
        return httpClient.post("$baseUrl/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun register(request: RegisterRequest): ResponseDto<UserResponse> {
        return httpClient.post("$baseUrl/users") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun refreshToken(request: RefreshTokenRequest): ResponseDto<LoginResponse> {
        return httpClient.post("$baseUrl/auth/refresh-token") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getCurrentUser(): ResponseDto<UserResponse> {
        return httpClient.get("$baseUrl/auth/me").body()
    }

    suspend fun logout(): ResponseDto<Unit> {
        return httpClient.delete("$baseUrl/users/me").body()
    }
}
