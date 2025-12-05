package com.shoot.app.presentation.common

sealed interface UiState<out T> {
    data object Idle : UiState<Nothing>
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String, val throwable: Throwable? = null) : UiState<Nothing>
}

// Extension functions for easier state handling
fun <T> UiState<T>.getDataOrNull(): T? = when (this) {
    is UiState.Success -> data
    else -> null
}

fun <T> UiState<T>.isLoading(): Boolean = this is UiState.Loading

fun <T> UiState<T>.isSuccess(): Boolean = this is UiState.Success

fun <T> UiState<T>.isError(): Boolean = this is UiState.Error

fun <T> UiState<T>.getErrorMessage(): String? = when (this) {
    is UiState.Error -> message
    else -> null
}
