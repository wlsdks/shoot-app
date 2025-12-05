package com.shoot.app.domain.exception

sealed class ShootException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    // Network Exceptions
    sealed class NetworkException(message: String, cause: Throwable? = null) : ShootException(message, cause) {
        class NoInternetConnection : NetworkException("인터넷 연결을 확인해주세요")
        class Timeout : NetworkException("요청 시간이 초과되었습니다")
        class ServerError(val code: Int, message: String) : NetworkException("서버 오류가 발생했습니다 (코드: $code) - $message")
        class Unknown(cause: Throwable?) : NetworkException("네트워크 오류가 발생했습니다", cause)
    }

    // Authentication Exceptions
    sealed class AuthException(message: String, cause: Throwable? = null) : ShootException(message, cause) {
        class Unauthorized : AuthException("로그인이 필요합니다")
        class InvalidCredentials : AuthException("아이디 또는 비밀번호가 올바르지 않습니다")
        class TokenExpired : AuthException("세션이 만료되었습니다. 다시 로그인해주세요")
        class UserNotFound : AuthException("사용자를 찾을 수 없습니다")
        class UserAlreadyExists : AuthException("이미 존재하는 사용자입니다")
    }

    // Validation Exceptions
    sealed class ValidationException(message: String) : ShootException(message) {
        class InvalidUsername : ValidationException("아이디는 3-20자 이내여야 합니다")
        class InvalidPassword : ValidationException("비밀번호는 최소 8자 이상이어야 합니다")
        class InvalidEmail : ValidationException("올바른 이메일 형식이 아닙니다")
        class InvalidNickname : ValidationException("닉네임은 2-30자 이내여야 합니다")
        class EmptyField(fieldName: String) : ValidationException("$fieldName 항목을 입력해주세요")
        class InvalidFormat(fieldName: String) : ValidationException("$fieldName 형식이 올바르지 않습니다")
    }

    // Chat Exceptions
    sealed class ChatException(message: String, cause: Throwable? = null) : ShootException(message, cause) {
        class RoomNotFound : ChatException("채팅방을 찾을 수 없습니다")
        class MessageSendFailed : ChatException("메시지 전송에 실패했습니다")
        class MessageNotFound : ChatException("메시지를 찾을 수 없습니다")
        class WebSocketConnectionFailed : ChatException("실시간 연결에 실패했습니다")
        class NotParticipant : ChatException("채팅방 참여자가 아닙니다")
    }

    // Friend Exceptions
    sealed class FriendException(message: String) : ShootException(message) {
        class AlreadyFriends : FriendException("이미 친구입니다")
        class RequestAlreadySent : FriendException("이미 친구 요청을 보냈습니다")
        class RequestNotFound : FriendException("친구 요청을 찾을 수 없습니다")
        class UserBlocked : FriendException("차단된 사용자입니다")
    }

    // Local Storage Exceptions
    sealed class StorageException(message: String, cause: Throwable? = null) : ShootException(message, cause) {
        class ReadFailed : StorageException("데이터를 읽는 중 오류가 발생했습니다")
        class WriteFailed : StorageException("데이터를 저장하는 중 오류가 발생했습니다")
        class NotFound : StorageException("데이터를 찾을 수 없습니다")
    }

    // Unknown Exception
    class Unknown(message: String = "알 수 없는 오류가 발생했습니다", cause: Throwable? = null) : ShootException(message, cause)
}

// Extension function to get user-friendly message
fun ShootException.getUserMessage(): String = when (this) {
    is ShootException.NetworkException.NoInternetConnection -> "인터넷 연결을 확인해주세요"
    is ShootException.NetworkException.Timeout -> "요청 시간이 초과되었습니다. 다시 시도해주세요"
    is ShootException.NetworkException.ServerError -> "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요"
    is ShootException.NetworkException.Unknown -> "네트워크 오류가 발생했습니다"

    is ShootException.AuthException.Unauthorized -> "로그인이 필요합니다"
    is ShootException.AuthException.InvalidCredentials -> "아이디 또는 비밀번호가 올바르지 않습니다"
    is ShootException.AuthException.TokenExpired -> "세션이 만료되었습니다. 다시 로그인해주세요"
    is ShootException.AuthException.UserNotFound -> "사용자를 찾을 수 없습니다"
    is ShootException.AuthException.UserAlreadyExists -> "이미 존재하는 사용자입니다"

    is ShootException.ValidationException -> this.message ?: "입력값이 올바르지 않습니다"

    is ShootException.ChatException.RoomNotFound -> "채팅방을 찾을 수 없습니다"
    is ShootException.ChatException.MessageSendFailed -> "메시지 전송에 실패했습니다"
    is ShootException.ChatException.MessageNotFound -> "메시지를 찾을 수 없습니다"
    is ShootException.ChatException.WebSocketConnectionFailed -> "실시간 연결에 실패했습니다"
    is ShootException.ChatException.NotParticipant -> "채팅방 참여자가 아닙니다"

    is ShootException.FriendException.AlreadyFriends -> "이미 친구입니다"
    is ShootException.FriendException.RequestAlreadySent -> "이미 친구 요청을 보냈습니다"
    is ShootException.FriendException.RequestNotFound -> "친구 요청을 찾을 수 없습니다"
    is ShootException.FriendException.UserBlocked -> "차단된 사용자입니다"

    is ShootException.StorageException.ReadFailed -> "데이터를 읽는 중 오류가 발생했습니다"
    is ShootException.StorageException.WriteFailed -> "데이터를 저장하는 중 오류가 발생했습니다"
    is ShootException.StorageException.NotFound -> "데이터를 찾을 수 없습니다"

    is ShootException.Unknown -> this.message ?: "알 수 없는 오류가 발생했습니다"
}
