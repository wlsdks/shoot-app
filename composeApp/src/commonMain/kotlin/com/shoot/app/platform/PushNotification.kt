package com.shoot.app.platform

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * 푸시 알림 데이터
 */
data class PushNotificationData(
    val title: String,
    val body: String,
    val data: Map<String, String> = emptyMap(),
    val notificationId: Int = 0,
    val channelId: String = "default",
    val groupId: String? = null
)

/**
 * 알림 채널 정보
 */
data class NotificationChannel(
    val id: String,
    val name: String,
    val description: String,
    val importance: NotificationImportance = NotificationImportance.DEFAULT
)

enum class NotificationImportance {
    MIN,
    LOW,
    DEFAULT,
    HIGH,
    MAX
}

/**
 * 알림 권한 상태
 */
sealed class NotificationPermissionState {
    data object Granted : NotificationPermissionState()
    data object Denied : NotificationPermissionState()
    data object NotDetermined : NotificationPermissionState()
}

/**
 * 푸시 알림 관리자 인터페이스
 */
expect class PushNotificationManager {
    /**
     * FCM/APNs 토큰 획득
     */
    suspend fun getToken(): String?

    /**
     * 토큰 갱신 관찰
     */
    fun observeTokenRefresh(): Flow<String>

    /**
     * 알림 권한 요청
     */
    suspend fun requestPermission(): NotificationPermissionState

    /**
     * 현재 알림 권한 상태 확인
     */
    fun getPermissionState(): NotificationPermissionState

    /**
     * 로컬 알림 표시
     */
    fun showLocalNotification(notification: PushNotificationData)

    /**
     * 알림 채널 생성 (Android)
     */
    fun createNotificationChannel(channel: NotificationChannel)

    /**
     * 모든 알림 제거
     */
    fun clearAllNotifications()

    /**
     * 특정 알림 제거
     */
    fun cancelNotification(notificationId: Int)

    /**
     * 토픽 구독 (예: 채팅방별 알림)
     */
    suspend fun subscribeToTopic(topic: String)

    /**
     * 토픽 구독 해제
     */
    suspend fun unsubscribeFromTopic(topic: String)
}

/**
 * 알림 설정
 */
data class NotificationSettings(
    val enabled: Boolean = true,
    val messageNotifications: Boolean = true,
    val friendRequestNotifications: Boolean = true,
    val mentionNotifications: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val showPreview: Boolean = true
)

/**
 * 알림 설정 관리자
 */
interface NotificationSettingsManager {
    fun getSettings(): NotificationSettings
    suspend fun updateSettings(settings: NotificationSettings)
    fun observeSettings(): Flow<NotificationSettings>
}

/**
 * 알림 설정 관리자 기본 구현
 */
class NotificationSettingsManagerImpl : NotificationSettingsManager {
    private val _settings = MutableStateFlow(NotificationSettings())

    override fun getSettings(): NotificationSettings = _settings.value

    override suspend fun updateSettings(settings: NotificationSettings) {
        _settings.value = settings
        // TODO: 로컬 저장소에 저장
    }

    override fun observeSettings(): Flow<NotificationSettings> = _settings
}
