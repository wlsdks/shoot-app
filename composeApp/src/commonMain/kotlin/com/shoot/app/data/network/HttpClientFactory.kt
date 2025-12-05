package com.shoot.app.data.network

import com.shoot.app.data.local.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

object HttpClientFactory {
    fun create(tokenManager: TokenManager? = null): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }

            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.INFO
            }

            // Auth Interceptor - Add JWT token to all requests
            if (tokenManager != null) {
                defaultRequest {
                    // Add auth header for all requests
                    val token = runBlocking { tokenManager.getAccessToken() }
                    if (!token.isNullOrEmpty()) {
                        header(HttpHeaders.Authorization, "Bearer $token")
                    }
                }
            }
        }
    }
}
