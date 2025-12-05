package com.shoot.app.utils

import com.shoot.app.domain.exception.ShootException
import com.shoot.app.domain.exception.getUserMessage
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import java.net.UnknownHostException

object ErrorHandler {

    fun handleException(throwable: Throwable): ShootException {
        // Don't handle CancellationException
        if (throwable is CancellationException) {
            throw throwable
        }

        return when (throwable) {
            // Already a ShootException
            is ShootException -> throwable

            // Network Exceptions
            is UnknownHostException -> ShootException.NetworkException.NoInternetConnection()
            is ConnectTimeoutException, is SocketTimeoutException -> ShootException.NetworkException.Timeout()

            // Ktor Client Exceptions
            is ClientRequestException -> {
                when (throwable.response.status) {
                    HttpStatusCode.Unauthorized -> ShootException.AuthException.Unauthorized()
                    HttpStatusCode.Forbidden -> ShootException.AuthException.Unauthorized()
                    HttpStatusCode.NotFound -> ShootException.NetworkException.ServerError(
                        404,
                        "요청한 리소스를 찾을 수 없습니다"
                    )
                    HttpStatusCode.BadRequest -> ShootException.ValidationException.InvalidFormat("요청")
                    else -> ShootException.NetworkException.ServerError(
                        throwable.response.status.value,
                        throwable.message
                    )
                }
            }

            is ServerResponseException -> {
                ShootException.NetworkException.ServerError(
                    throwable.response.status.value,
                    "서버에서 오류가 발생했습니다"
                )
            }

            // Unknown Exception
            else -> ShootException.Unknown(throwable.message ?: "알 수 없는 오류가 발생했습니다", throwable)
        }
    }

    fun getErrorMessage(throwable: Throwable): String {
        val exception = if (throwable is ShootException) {
            throwable
        } else {
            handleException(throwable)
        }

        return exception.getUserMessage()
    }
}

// Extension function for easier use in ViewModels
suspend fun <T> safeApiCall(
    apiCall: suspend () -> T
): Result<T> {
    return try {
        Result.success(apiCall())
    } catch (e: Exception) {
        Result.failure(ErrorHandler.handleException(e))
    }
}

// Extension function to handle Result with error mapping
fun <T> Result<T>.mapError(): Result<T> {
    return this.onFailure { throwable ->
        if (throwable !is ShootException) {
            Result.failure<T>(ErrorHandler.handleException(throwable))
        }
    }
}
