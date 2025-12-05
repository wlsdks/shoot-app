package com.shoot.app.presentation.chat

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.shoot.app.data.local.TokenManager
import com.shoot.app.data.remote.dto.message.MessageContentRequest
import com.shoot.app.data.remote.dto.message.SendMessageRequest
import com.shoot.app.data.repository.MessageRepository
import com.shoot.app.data.websocket.ConnectionState
import com.shoot.app.data.websocket.WebSocketClient
import com.shoot.app.domain.model.Message
import com.shoot.app.domain.model.MessageContent
import com.shoot.app.domain.model.MessageStatus
import com.shoot.app.domain.model.MessageType
import com.shoot.app.presentation.common.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class ChatViewModel(
    private val roomId: Long,
    private val currentUserId: Long,
    private val messageRepository: MessageRepository,
    private val webSocketClient: WebSocketClient,
    private val tokenManager: TokenManager
) : ScreenModel {

    private val _messagesState = MutableStateFlow<UiState<List<Message>>>(UiState.Idle)
    val messagesState: StateFlow<UiState<List<Message>>> = _messagesState.asStateFlow()

    private val _sendingState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val sendingState: StateFlow<UiState<Unit>> = _sendingState.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    val connectionState: StateFlow<ConnectionState> = webSocketClient.connectionState

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    init {
        loadMessages()
        connectWebSocket()
        observeIncomingMessages()
    }

    /**
     * 메시지 목록 로드
     */
    private fun loadMessages() {
        screenModelScope.launch {
            _messagesState.value = UiState.Loading

            messageRepository.getMessages(roomId, page = 0, size = 50)
                .onSuccess { result ->
                    _messages.value = result.messages.sortedBy { it.createdAt }
                    _messagesState.value = UiState.Success(result.messages)
                }
                .onFailure { error ->
                    _messagesState.value = UiState.Error(error.message ?: "Failed to load messages")
                }
        }
    }

    /**
     * WebSocket 연결
     */
    private fun connectWebSocket() {
        screenModelScope.launch {
            val token = tokenManager.getAccessToken() ?: return@launch

            try {
                webSocketClient.connect(token)
                // 채팅방 토픽 구독
                webSocketClient.subscribe("/topic/chat/$roomId")
            } catch (e: Exception) {
                println("[ChatViewModel] WebSocket connection failed: ${e.message}")
            }
        }
    }

    /**
     * 수신 메시지 관찰
     */
    private fun observeIncomingMessages() {
        screenModelScope.launch {
            webSocketClient.incomingMessages.collect { messageJson ->
                try {
                    // TODO: JSON 파싱하여 Message 객체로 변환
                    // 현재는 간단하게 메시지 목록 새로고침
                    loadMessages()
                } catch (e: Exception) {
                    println("[ChatViewModel] Failed to parse incoming message: ${e.message}")
                }
            }
        }
    }

    /**
     * 메시지 전송
     */
    fun sendMessage(text: String) {
        if (text.isBlank()) return

        screenModelScope.launch {
            _sendingState.value = UiState.Loading

            // 임시 ID 생성
            val tempId = "temp_${System.currentTimeMillis()}"

            // 로컬에 임시 메시지 추가 (Optimistic UI)
            val tempMessage = Message(
                id = tempId,
                roomId = roomId,
                senderId = currentUserId,
                content = MessageContent(
                    text = text,
                    type = MessageType.TEXT
                ),
                status = MessageStatus.SENDING,
                createdAt = getCurrentTimestamp(),
                tempId = tempId
            )

            _messages.value = _messages.value + tempMessage

            // WebSocket으로 전송 시도
            try {
                val request = SendMessageRequest(
                    tempId = tempId,
                    roomId = roomId,
                    senderId = currentUserId,
                    content = MessageContentRequest(
                        text = text,
                        type = "TEXT"
                    )
                )

                // STOMP 메시지 전송
                val jsonString = json.encodeToString(SendMessageRequest.serializer(), request)
                webSocketClient.sendToDestination("/app/chat", jsonString)

                _sendingState.value = UiState.Success(Unit)

            } catch (e: Exception) {
                println("[ChatViewModel] WebSocket send failed, trying REST API: ${e.message}")

                // WebSocket 실패 시 REST API 사용 (fallback)
                val request = SendMessageRequest(
                    tempId = tempId,
                    roomId = roomId,
                    senderId = currentUserId,
                    content = MessageContentRequest(
                        text = text,
                        type = "TEXT"
                    )
                )

                messageRepository.sendMessage(request)
                    .onSuccess { message ->
                        // 임시 메시지를 실제 메시지로 교체
                        _messages.value = _messages.value.map {
                            if (it.tempId == tempId) message else it
                        }
                        _sendingState.value = UiState.Success(Unit)
                    }
                    .onFailure { error ->
                        // 전송 실패 - 임시 메시지 상태 업데이트
                        _messages.value = _messages.value.map {
                            if (it.tempId == tempId) {
                                it.copy(status = MessageStatus.FAILED)
                            } else it
                        }
                        _sendingState.value = UiState.Error(error.message ?: "Failed to send message")
                    }
            }
        }
    }

    /**
     * 메시지 다시 전송 (실패한 메시지)
     */
    fun retryMessage(message: Message) {
        if (message.status != MessageStatus.FAILED) return
        sendMessage(message.content.text)
    }

    /**
     * 더 많은 메시지 로드 (페이지네이션)
     */
    fun loadMoreMessages() {
        // TODO: 페이지네이션 구현
    }

    /**
     * 메시지 편집
     */
    fun editMessage(messageId: String, newContent: String) {
        screenModelScope.launch {
            messageRepository.editMessage(messageId, newContent, currentUserId)
                .onSuccess { updatedMessage ->
                    _messages.value = _messages.value.map {
                        if (it.id == messageId) updatedMessage else it
                    }
                }
                .onFailure { error ->
                    println("[ChatViewModel] Failed to edit message: ${error.message}")
                }
        }
    }

    /**
     * 메시지 삭제
     */
    fun deleteMessage(messageId: String) {
        screenModelScope.launch {
            messageRepository.deleteMessage(messageId, currentUserId)
                .onSuccess { deletedMessage ->
                    _messages.value = _messages.value.map {
                        if (it.id == messageId) deletedMessage else it
                    }
                }
                .onFailure { error ->
                    println("[ChatViewModel] Failed to delete message: ${error.message}")
                }
        }
    }

    /**
     * 메시지에 반응 추가/제거 (토글)
     */
    fun toggleReaction(messageId: String, reactionType: String) {
        screenModelScope.launch {
            messageRepository.toggleReaction(messageId, currentUserId, reactionType)
                .onSuccess { updatedMessage ->
                    _messages.value = _messages.value.map {
                        if (it.id == messageId) updatedMessage else it
                    }
                }
                .onFailure { error ->
                    println("[ChatViewModel] Failed to toggle reaction: ${error.message}")
                }
        }
    }

    /**
     * 현재 타임스탬프 (ISO 8601 형식)
     */
    private fun getCurrentTimestamp(): String {
        return kotlinx.datetime.Clock.System.now().toString()
    }

    /**
     * WebSocket 연결 해제
     */
    override fun onDispose() {
        super.onDispose()
        screenModelScope.launch {
            webSocketClient.disconnect()
        }
    }
}
