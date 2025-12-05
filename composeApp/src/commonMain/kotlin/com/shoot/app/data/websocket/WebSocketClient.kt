package com.shoot.app.data.websocket

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WebSocketClient(
    private val client: HttpClient,
    private val baseUrl: String = "localhost:8100"
) {
    private fun log(message: String) = println("[WebSocketClient] $message")
    private fun logError(message: String, error: Throwable? = null) {
        println("[WebSocketClient ERROR] $message")
        error?.printStackTrace()
    }
    private var webSocketSession: WebSocketSession? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    private val _incomingMessages = MutableSharedFlow<String>(replay = 0)
    val incomingMessages: Flow<String> = _incomingMessages

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * WebSocket 연결
     */
    suspend fun connect(token: String) {
        if (_connectionState.value is ConnectionState.Connected) {
            log("Already connected to WebSocket")
            return
        }

        try {
            _connectionState.value = ConnectionState.Connecting

            client.webSocket(
                host = baseUrl.substringBefore(":"),
                port = baseUrl.substringAfter(":").toIntOrNull() ?: 8100,
                path = "/ws",
                request = {
                    url {
                        parameters.append("token", token)
                    }
                }
            ) {
                webSocketSession = this
                _connectionState.value = ConnectionState.Connected
                log("WebSocket connected")

                // Listen for incoming messages
                scope.launch {
                    try {
                        for (frame in incoming) {
                            when (frame) {
                                is Frame.Text -> {
                                    val text = frame.readText()
                                    log("Received WebSocket message: $text")
                                    _incomingMessages.emit(text)
                                }
                                is Frame.Close -> {
                                    log("WebSocket closed: ${frame.readReason()}")
                                    _connectionState.value = ConnectionState.Disconnected
                                    break
                                }
                                else -> {}
                            }
                        }
                    } catch (e: Exception) {
                        logError("Error receiving WebSocket messages", e)
                        _connectionState.value = ConnectionState.Error(e.message ?: "Unknown error")
                    }
                }
            }
        } catch (e: Exception) {
            logError("WebSocket connection failed", e)
            _connectionState.value = ConnectionState.Error(e.message ?: "Connection failed")
        }
    }

    /**
     * 메시지 전송
     */
    suspend fun send(message: String) {
        try {
            webSocketSession?.send(Frame.Text(message))
            log("Sent WebSocket message: $message")
        } catch (e: Exception) {
            logError("Failed to send WebSocket message", e)
            throw e
        }
    }

    /**
     * JSON 문자열로 전송
     */
    suspend fun sendJson(jsonString: String) {
        send(jsonString)
    }

    /**
     * 특정 토픽 구독 (STOMP 프로토콜 시뮬레이션)
     */
    suspend fun subscribe(destination: String) {
        val subscribeMessage = buildStompFrame(
            command = "SUBSCRIBE",
            headers = mapOf(
                "id" to "sub-${System.currentTimeMillis()}",
                "destination" to destination
            )
        )
        send(subscribeMessage)
        log("Subscribed to $destination")
    }

    /**
     * 메시지 발행 (STOMP 프로토콜 시뮬레이션)
     */
    suspend fun sendToDestination(destination: String, body: String) {
        val sendMessage = buildStompFrame(
            command = "SEND",
            headers = mapOf("destination" to destination),
            body = body
        )
        send(sendMessage)
    }

    /**
     * 연결 해제
     */
    suspend fun disconnect() {
        try {
            val disconnectMessage = buildStompFrame("DISCONNECT")
            webSocketSession?.send(Frame.Text(disconnectMessage))
            webSocketSession?.close(CloseReason(CloseReason.Codes.NORMAL, "Client disconnect"))
            webSocketSession = null
            _connectionState.value = ConnectionState.Disconnected
            log("WebSocket disconnected")
        } catch (e: Exception) {
            logError("Error disconnecting WebSocket", e)
        }
    }

    /**
     * STOMP 프레임 생성
     */
    private fun buildStompFrame(
        command: String,
        headers: Map<String, String> = emptyMap(),
        body: String = ""
    ): String {
        val frame = buildString {
            append(command)
            append("\n")
            headers.forEach { (key, value) ->
                append("$key:$value\n")
            }
            append("\n")
            if (body.isNotEmpty()) {
                append(body)
            }
            append("\u0000") // NULL terminator
        }
        return frame
    }

    fun cleanup() {
        scope.cancel()
    }
}

sealed class ConnectionState {
    object Disconnected : ConnectionState()
    object Connecting : ConnectionState()
    object Connected : ConnectionState()
    data class Error(val message: String) : ConnectionState()
}
