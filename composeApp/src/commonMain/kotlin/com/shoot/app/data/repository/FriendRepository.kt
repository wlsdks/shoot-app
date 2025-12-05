package com.shoot.app.data.repository

import com.shoot.app.data.remote.api.FriendApiService
import com.shoot.app.data.remote.dto.friend.toDomain
import com.shoot.app.domain.model.Friend
import com.shoot.app.domain.model.FriendRequest
import com.shoot.app.utils.safeApiCall

interface FriendRepository {
    suspend fun getFriends(userId: Long): Result<List<Friend>>
    suspend fun getIncomingRequests(userId: Long): Result<List<FriendRequest>>
    suspend fun getOutgoingRequests(userId: Long): Result<List<FriendRequest>>
    suspend fun sendFriendRequest(senderId: Long, recipientId: Long): Result<FriendRequest>
    suspend fun acceptFriendRequest(requestId: String, responderId: Long): Result<FriendRequest>
    suspend fun rejectFriendRequest(requestId: String, responderId: Long): Result<FriendRequest>
    suspend fun cancelFriendRequest(requestId: String, senderId: Long): Result<Unit>
    suspend fun removeFriend(userId: Long, friendId: Long): Result<Unit>
    suspend fun searchFriends(userId: Long, query: String): Result<List<Friend>>
}

class FriendRepositoryImpl(
    private val friendApiService: FriendApiService
) : FriendRepository {

    override suspend fun getFriends(userId: Long): Result<List<Friend>> {
        return safeApiCall {
            val response = friendApiService.getFriends(userId)

            if (response.success && response.data != null) {
                response.data.map { it.toDomain() }
            } else {
                throw Exception(response.message ?: "Failed to get friends")
            }
        }
    }

    override suspend fun getIncomingRequests(userId: Long): Result<List<FriendRequest>> {
        return safeApiCall {
            val response = friendApiService.getIncomingRequests(userId)

            if (response.success && response.data != null) {
                response.data.map { it.toDomain() }
            } else {
                throw Exception(response.message ?: "Failed to get incoming requests")
            }
        }
    }

    override suspend fun getOutgoingRequests(userId: Long): Result<List<FriendRequest>> {
        return safeApiCall {
            val response = friendApiService.getOutgoingRequests(userId)

            if (response.success && response.data != null) {
                response.data.map { it.toDomain() }
            } else {
                throw Exception(response.message ?: "Failed to get outgoing requests")
            }
        }
    }

    override suspend fun sendFriendRequest(senderId: Long, recipientId: Long): Result<FriendRequest> {
        return safeApiCall {
            val response = friendApiService.sendFriendRequest(senderId, recipientId)

            if (response.success && response.data != null) {
                response.data.toDomain()
            } else {
                throw Exception(response.message ?: "Failed to send friend request")
            }
        }
    }

    override suspend fun acceptFriendRequest(requestId: String, responderId: Long): Result<FriendRequest> {
        return safeApiCall {
            val response = friendApiService.acceptFriendRequest(requestId, responderId)

            if (response.success && response.data != null) {
                response.data.toDomain()
            } else {
                throw Exception(response.message ?: "Failed to accept friend request")
            }
        }
    }

    override suspend fun rejectFriendRequest(requestId: String, responderId: Long): Result<FriendRequest> {
        return safeApiCall {
            val response = friendApiService.rejectFriendRequest(requestId, responderId)

            if (response.success && response.data != null) {
                response.data.toDomain()
            } else {
                throw Exception(response.message ?: "Failed to reject friend request")
            }
        }
    }

    override suspend fun cancelFriendRequest(requestId: String, senderId: Long): Result<Unit> {
        return safeApiCall {
            val response = friendApiService.cancelFriendRequest(requestId, senderId)

            if (response.success) {
                Unit
            } else {
                throw Exception(response.message ?: "Failed to cancel friend request")
            }
        }
    }

    override suspend fun removeFriend(userId: Long, friendId: Long): Result<Unit> {
        return safeApiCall {
            val response = friendApiService.removeFriend(userId, friendId)

            if (response.success) {
                Unit
            } else {
                throw Exception(response.message ?: "Failed to remove friend")
            }
        }
    }

    override suspend fun searchFriends(userId: Long, query: String): Result<List<Friend>> {
        return safeApiCall {
            val response = friendApiService.searchFriends(userId, query)

            if (response.success && response.data != null) {
                response.data.map { it.toDomain() }
            } else {
                throw Exception(response.message ?: "Failed to search friends")
            }
        }
    }
}
