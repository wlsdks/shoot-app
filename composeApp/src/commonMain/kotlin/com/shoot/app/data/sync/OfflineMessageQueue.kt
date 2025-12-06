package com.shoot.app.data.sync

import com.shoot.app.domain.model.Message
import com.shoot.app.domain.model.MessageContent
import com.shoot.app.domain.model.MessageStatus
import com.shoot.app.domain.model.MessageType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock

/**
 * 오프라인 메시지 큐
 * 오프라인 상태에서 보낸 메시지를 저장하고, 온라인 상태가 되면 전송합니다.
 */
interface OfflineMessageQueue {
    /**
     * 메시지를 큐에 추가
     */
    suspend fun enqueue(message: PendingMessage)

    /**
     * 큐에서 메시지 제거
     */
    suspend fun dequeue(messageId: String)

    /**
     * 모든 대기 중인 메시지 가져오기
     */
    fun getPendingMessages(): Flow<List<PendingMessage>>

    /**
     * 특정 채팅방의 대기 중인 메시지
     */
    fun getPendingMessagesForRoom(roomId: Long): Flow<List<PendingMessage>>

    /**
     * 메시지 상태 업데이트
     */
    suspend fun updateStatus(messageId: String, status: MessageQueueStatus)

    /**
     * 대기 중인 메시지 수
     */
    fun getPendingCount(): Flow<Int>

    /**
     * 모든 대기 중인 메시지 처리
     */
    suspend fun processAll(
        onSend: suspend (PendingMessage) -> Result<Message>
    )
}

/**
 * 대기 중인 메시지
 */
data class PendingMessage(
    val id: String,
    val roomId: Long,
    val senderId: Long,
    val content: String,
    val messageType: MessageType = MessageType.TEXT,
    val attachmentIds: List<String> = emptyList(),
    val replyToMessageId: String? = null,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val status: MessageQueueStatus = MessageQueueStatus.PENDING,
    val retryCount: Int = 0
)

enum class MessageQueueStatus {
    PENDING,    // 전송 대기
    SENDING,    // 전송 중
    SENT,       // 전송 완료
    FAILED      // 전송 실패
}

/**
 * 오프라인 메시지 큐 구현
 */
class OfflineMessageQueueImpl : OfflineMessageQueue {

    private val _pendingMessages = MutableStateFlow<List<PendingMessage>>(emptyList())

    override suspend fun enqueue(message: PendingMessage) {
        val currentList = _pendingMessages.value.toMutableList()
        currentList.add(message)
        _pendingMessages.value = currentList

        // TODO: 로컬 DB에 저장
    }

    override suspend fun dequeue(messageId: String) {
        _pendingMessages.value = _pendingMessages.value.filter { it.id != messageId }

        // TODO: 로컬 DB에서 삭제
    }

    override fun getPendingMessages(): Flow<List<PendingMessage>> {
        return _pendingMessages.asStateFlow()
    }

    override fun getPendingMessagesForRoom(roomId: Long): Flow<List<PendingMessage>> {
        return MutableStateFlow(
            _pendingMessages.value.filter { it.roomId == roomId }
        )
    }

    override suspend fun updateStatus(messageId: String, status: MessageQueueStatus) {
        _pendingMessages.value = _pendingMessages.value.map {
            if (it.id == messageId) it.copy(status = status) else it
        }

        // TODO: 로컬 DB 업데이트
    }

    override fun getPendingCount(): Flow<Int> {
        return MutableStateFlow(_pendingMessages.value.size)
    }

    override suspend fun processAll(
        onSend: suspend (PendingMessage) -> Result<Message>
    ) {
        val messages = _pendingMessages.value.filter {
            it.status == MessageQueueStatus.PENDING || it.status == MessageQueueStatus.FAILED
        }

        for (message in messages) {
            updateStatus(message.id, MessageQueueStatus.SENDING)

            onSend(message)
                .onSuccess {
                    dequeue(message.id)
                }
                .onFailure {
                    if (message.retryCount < 3) {
                        _pendingMessages.value = _pendingMessages.value.map { m ->
                            if (m.id == message.id) {
                                m.copy(
                                    status = MessageQueueStatus.FAILED,
                                    retryCount = m.retryCount + 1
                                )
                            } else m
                        }
                    } else {
                        // 최대 재시도 횟수 초과 - 실패 상태로 유지
                        updateStatus(message.id, MessageQueueStatus.FAILED)
                    }
                }
        }
    }
}

/**
 * PendingMessage를 Message로 변환
 */
fun PendingMessage.toMessage(): Message {
    return Message(
        id = id,
        roomId = roomId,
        senderId = senderId,
        content = MessageContent(
            text = content,
            type = messageType
        ),
        status = when (status) {
            MessageQueueStatus.PENDING -> MessageStatus.SENDING
            MessageQueueStatus.SENDING -> MessageStatus.SENDING
            MessageQueueStatus.SENT -> MessageStatus.SENT_TO_KAFKA
            MessageQueueStatus.FAILED -> MessageStatus.FAILED
        },
        replyToMessageId = replyToMessageId,
        createdAt = Clock.System.now().toString(),
        tempId = id
    )
}
