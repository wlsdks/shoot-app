package com.shoot.app.data.sync

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock

/**
 * 동기화 상태
 */
sealed class SyncState {
    data object Idle : SyncState()
    data object Syncing : SyncState()
    data class Success(val timestamp: Long) : SyncState()
    data class Error(val message: String, val timestamp: Long) : SyncState()
}

/**
 * 동기화 대상 엔티티 타입
 */
enum class SyncEntityType {
    MESSAGE,
    CHAT_ROOM,
    FRIEND,
    USER_PROFILE,
    SETTINGS
}

/**
 * 대기 중인 동기화 작업
 */
data class PendingSyncOperation(
    val id: String,
    val entityType: SyncEntityType,
    val operation: SyncOperationType,
    val entityId: String,
    val data: String, // JSON 직렬화된 데이터
    val createdAt: Long,
    val retryCount: Int = 0,
    val maxRetries: Int = 3
)

enum class SyncOperationType {
    CREATE,
    UPDATE,
    DELETE
}

/**
 * 오프라인 동기화 관리자
 */
interface SyncManager {
    /**
     * 현재 동기화 상태
     */
    fun getSyncState(): Flow<SyncState>

    /**
     * 전체 동기화 수행
     */
    suspend fun syncAll()

    /**
     * 특정 엔티티 타입만 동기화
     */
    suspend fun sync(entityType: SyncEntityType)

    /**
     * 대기 중인 작업 추가
     */
    suspend fun addPendingOperation(operation: PendingSyncOperation)

    /**
     * 대기 중인 작업 처리
     */
    suspend fun processPendingOperations()

    /**
     * 대기 중인 작업 수
     */
    fun getPendingOperationCount(): Flow<Int>

    /**
     * 네트워크 상태 관찰
     */
    fun observeNetworkState(): Flow<Boolean>

    /**
     * 마지막 동기화 시간
     */
    fun getLastSyncTime(entityType: SyncEntityType): Long?
}

/**
 * 기본 동기화 관리자 구현
 */
class SyncManagerImpl : SyncManager {

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    private val _pendingOperations = MutableStateFlow<List<PendingSyncOperation>>(emptyList())
    private val _isOnline = MutableStateFlow(true)
    private val _lastSyncTimes = mutableMapOf<SyncEntityType, Long>()

    override fun getSyncState(): Flow<SyncState> = _syncState.asStateFlow()

    override suspend fun syncAll() {
        if (_syncState.value is SyncState.Syncing) return

        _syncState.value = SyncState.Syncing

        try {
            // 1. 먼저 대기 중인 로컬 변경사항을 서버에 푸시
            processPendingOperations()

            // 2. 각 엔티티 타입별로 서버에서 데이터 가져오기
            SyncEntityType.entries.forEach { entityType ->
                sync(entityType)
            }

            val now = Clock.System.now().toEpochMilliseconds()
            _syncState.value = SyncState.Success(now)

        } catch (e: Exception) {
            val now = Clock.System.now().toEpochMilliseconds()
            _syncState.value = SyncState.Error(e.message ?: "동기화 실패", now)
        }
    }

    override suspend fun sync(entityType: SyncEntityType) {
        try {
            when (entityType) {
                SyncEntityType.MESSAGE -> syncMessages()
                SyncEntityType.CHAT_ROOM -> syncChatRooms()
                SyncEntityType.FRIEND -> syncFriends()
                SyncEntityType.USER_PROFILE -> syncUserProfile()
                SyncEntityType.SETTINGS -> syncSettings()
            }

            _lastSyncTimes[entityType] = Clock.System.now().toEpochMilliseconds()

        } catch (e: Exception) {
            println("[SyncManager] Failed to sync $entityType: ${e.message}")
            throw e
        }
    }

    override suspend fun addPendingOperation(operation: PendingSyncOperation) {
        val currentList = _pendingOperations.value.toMutableList()
        currentList.add(operation)
        _pendingOperations.value = currentList

        // TODO: 로컬 DB에 저장
    }

    override suspend fun processPendingOperations() {
        val operations = _pendingOperations.value.toList()

        for (operation in operations) {
            try {
                when (operation.operation) {
                    SyncOperationType.CREATE -> handleCreate(operation)
                    SyncOperationType.UPDATE -> handleUpdate(operation)
                    SyncOperationType.DELETE -> handleDelete(operation)
                }

                // 성공 시 대기 목록에서 제거
                removePendingOperation(operation.id)

            } catch (e: Exception) {
                // 재시도 횟수 증가
                if (operation.retryCount < operation.maxRetries) {
                    updateRetryCount(operation.id, operation.retryCount + 1)
                } else {
                    // 최대 재시도 횟수 초과 - 로그 남기고 제거
                    println("[SyncManager] Max retries exceeded for operation ${operation.id}")
                    removePendingOperation(operation.id)
                }
            }
        }
    }

    override fun getPendingOperationCount(): Flow<Int> {
        return MutableStateFlow(_pendingOperations.value.size)
    }

    override fun observeNetworkState(): Flow<Boolean> = _isOnline.asStateFlow()

    override fun getLastSyncTime(entityType: SyncEntityType): Long? {
        return _lastSyncTimes[entityType]
    }

    // Private methods

    private suspend fun syncMessages() {
        // TODO: MessageRepository에서 마지막 동기화 이후 메시지 가져오기
    }

    private suspend fun syncChatRooms() {
        // TODO: ChatRoomRepository에서 채팅방 목록 동기화
    }

    private suspend fun syncFriends() {
        // TODO: FriendRepository에서 친구 목록 동기화
    }

    private suspend fun syncUserProfile() {
        // TODO: UserRepository에서 프로필 동기화
    }

    private suspend fun syncSettings() {
        // TODO: 설정 동기화
    }

    private suspend fun handleCreate(operation: PendingSyncOperation) {
        // TODO: 생성 작업 처리
    }

    private suspend fun handleUpdate(operation: PendingSyncOperation) {
        // TODO: 업데이트 작업 처리
    }

    private suspend fun handleDelete(operation: PendingSyncOperation) {
        // TODO: 삭제 작업 처리
    }

    private fun removePendingOperation(id: String) {
        _pendingOperations.value = _pendingOperations.value.filter { it.id != id }
    }

    private fun updateRetryCount(id: String, newCount: Int) {
        _pendingOperations.value = _pendingOperations.value.map {
            if (it.id == id) it.copy(retryCount = newCount) else it
        }
    }

    /**
     * 네트워크 상태 업데이트
     */
    fun updateNetworkState(isOnline: Boolean) {
        _isOnline.value = isOnline
    }
}
