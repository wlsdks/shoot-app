package com.shoot.app.presentation.settings

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.shoot.app.data.local.TokenManager
import com.shoot.app.data.repository.AuthRepository
import com.shoot.app.data.repository.UserRepository
import com.shoot.app.domain.model.User
import com.shoot.app.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) : ScreenModel {

    private val _userState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val userState: StateFlow<UiState<User>> = _userState.asStateFlow()

    private val _logoutState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val logoutState: StateFlow<UiState<Unit>> = _logoutState.asStateFlow()

    private val _updateProfileState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val updateProfileState: StateFlow<UiState<User>> = _updateProfileState.asStateFlow()

    private val _changePasswordState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val changePasswordState: StateFlow<UiState<Unit>> = _changePasswordState.asStateFlow()

    private val _deleteAccountState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val deleteAccountState: StateFlow<UiState<Unit>> = _deleteAccountState.asStateFlow()

    // Settings state
    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _darkModeEnabled = MutableStateFlow(false)
    val darkModeEnabled: StateFlow<Boolean> = _darkModeEnabled.asStateFlow()

    fun loadCurrentUser() {
        screenModelScope.launch {
            _userState.value = UiState.Loading

            authRepository.getCurrentUser()
                .onSuccess { user ->
                    _userState.value = UiState.Success(user)
                }
                .onFailure { error ->
                    _userState.value = UiState.Error(
                        message = error.message ?: "사용자 정보를 불러오지 못했습니다",
                        throwable = error
                    )
                }
        }
    }

    fun updateProfile(
        nickname: String? = null,
        bio: String? = null,
        profileImageUrl: String? = null
    ) {
        screenModelScope.launch {
            _updateProfileState.value = UiState.Loading

            userRepository.updateProfile(
                nickname = nickname,
                bio = bio,
                profileImageUrl = profileImageUrl
            )
                .onSuccess { user ->
                    _updateProfileState.value = UiState.Success(user)
                    _userState.value = UiState.Success(user)
                }
                .onFailure { error ->
                    _updateProfileState.value = UiState.Error(
                        message = error.message ?: "프로필 업데이트에 실패했습니다",
                        throwable = error
                    )
                }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        screenModelScope.launch {
            _changePasswordState.value = UiState.Loading

            userRepository.changePassword(currentPassword, newPassword)
                .onSuccess {
                    _changePasswordState.value = UiState.Success(Unit)
                }
                .onFailure { error ->
                    _changePasswordState.value = UiState.Error(
                        message = error.message ?: "비밀번호 변경에 실패했습니다",
                        throwable = error
                    )
                }
        }
    }

    fun logout() {
        screenModelScope.launch {
            _logoutState.value = UiState.Loading

            authRepository.logout()
                .onSuccess {
                    tokenManager.clearTokens()
                    _logoutState.value = UiState.Success(Unit)
                }
                .onFailure { error ->
                    // 서버 로그아웃 실패해도 로컬 토큰은 삭제
                    tokenManager.clearTokens()
                    _logoutState.value = UiState.Success(Unit)
                }
        }
    }

    fun deleteAccount() {
        screenModelScope.launch {
            _deleteAccountState.value = UiState.Loading

            userRepository.deleteAccount()
                .onSuccess {
                    tokenManager.clearTokens()
                    _deleteAccountState.value = UiState.Success(Unit)
                }
                .onFailure { error ->
                    _deleteAccountState.value = UiState.Error(
                        message = error.message ?: "회원 탈퇴에 실패했습니다",
                        throwable = error
                    )
                }
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        _notificationsEnabled.value = enabled
        // TODO: 서버에 알림 설정 저장
    }

    fun toggleDarkMode(enabled: Boolean) {
        _darkModeEnabled.value = enabled
        // TODO: 테마 설정 저장
    }

    fun resetUpdateProfileState() {
        _updateProfileState.value = UiState.Idle
    }

    fun resetChangePasswordState() {
        _changePasswordState.value = UiState.Idle
    }

    fun resetDeleteAccountState() {
        _deleteAccountState.value = UiState.Idle
    }
}
