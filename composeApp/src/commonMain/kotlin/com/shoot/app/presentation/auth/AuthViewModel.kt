package com.shoot.app.presentation.auth

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.shoot.app.data.local.TokenManager
import com.shoot.app.data.repository.AuthRepository
import com.shoot.app.domain.model.User
import com.shoot.app.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ScreenModel {

    private val _loginState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val loginState: StateFlow<UiState<User>> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val registerState: StateFlow<UiState<User>> = _registerState.asStateFlow()

    fun login(username: String, password: String) {
        screenModelScope.launch {
            _loginState.value = UiState.Loading

            authRepository.login(username, password)
                .onSuccess { loginData ->
                    // Save tokens
                    tokenManager.saveAccessToken(loginData.accessToken)
                    tokenManager.saveRefreshToken(loginData.refreshToken)

                    // Get current user info
                    authRepository.getCurrentUser()
                        .onSuccess { user ->
                            _loginState.value = UiState.Success(user)
                        }
                        .onFailure { error ->
                            _loginState.value = UiState.Error(
                                message = error.message ?: "Failed to get user info",
                                throwable = error
                            )
                        }
                }
                .onFailure { error ->
                    _loginState.value = UiState.Error(
                        message = error.message ?: "Login failed",
                        throwable = error
                    )
                }
        }
    }

    fun register(username: String, nickname: String, password: String, email: String) {
        screenModelScope.launch {
            _registerState.value = UiState.Loading

            authRepository.register(
                username = username,
                nickname = nickname,
                password = password,
                email = email,
                bio = null
            )
                .onSuccess { user ->
                    _registerState.value = UiState.Success(user)
                }
                .onFailure { error ->
                    _registerState.value = UiState.Error(
                        message = error.message ?: "Registration failed",
                        throwable = error
                    )
                }
        }
    }

    fun resetLoginState() {
        _loginState.value = UiState.Idle
    }

    fun resetRegisterState() {
        _registerState.value = UiState.Idle
    }
}
