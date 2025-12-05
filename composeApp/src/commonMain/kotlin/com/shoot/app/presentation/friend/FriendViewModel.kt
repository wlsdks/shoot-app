package com.shoot.app.presentation.friend

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.shoot.app.data.local.TokenManager
import com.shoot.app.data.repository.FriendRepository
import com.shoot.app.domain.model.Friend
import com.shoot.app.domain.model.FriendRequest
import com.shoot.app.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FriendViewModel(
    private val friendRepository: FriendRepository,
    private val tokenManager: TokenManager
) : ScreenModel {

    private val _friendsState = MutableStateFlow<UiState<List<Friend>>>(UiState.Idle)
    val friendsState: StateFlow<UiState<List<Friend>>> = _friendsState.asStateFlow()

    private val _incomingRequestsState = MutableStateFlow<UiState<List<FriendRequest>>>(UiState.Idle)
    val incomingRequestsState: StateFlow<UiState<List<FriendRequest>>> = _incomingRequestsState.asStateFlow()

    private val _outgoingRequestsState = MutableStateFlow<UiState<List<FriendRequest>>>(UiState.Idle)
    val outgoingRequestsState: StateFlow<UiState<List<FriendRequest>>> = _outgoingRequestsState.asStateFlow()

    private val _searchResultsState = MutableStateFlow<UiState<List<Friend>>>(UiState.Idle)
    val searchResultsState: StateFlow<UiState<List<Friend>>> = _searchResultsState.asStateFlow()

    private val _sendRequestState = MutableStateFlow<UiState<FriendRequest>>(UiState.Idle)
    val sendRequestState: StateFlow<UiState<FriendRequest>> = _sendRequestState.asStateFlow()

    private val _acceptRequestState = MutableStateFlow<UiState<FriendRequest>>(UiState.Idle)
    val acceptRequestState: StateFlow<UiState<FriendRequest>> = _acceptRequestState.asStateFlow()

    private val _rejectRequestState = MutableStateFlow<UiState<FriendRequest>>(UiState.Idle)
    val rejectRequestState: StateFlow<UiState<FriendRequest>> = _rejectRequestState.asStateFlow()

    private val _cancelRequestState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val cancelRequestState: StateFlow<UiState<Unit>> = _cancelRequestState.asStateFlow()

    private val _removeFriendState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val removeFriendState: StateFlow<UiState<Unit>> = _removeFriendState.asStateFlow()

    fun loadFriends() {
        screenModelScope.launch {
            _friendsState.value = UiState.Loading

            // Get current user ID from token manager
            val userId = getCurrentUserId()
            if (userId == null) {
                _friendsState.value = UiState.Error("User not logged in")
                return@launch
            }

            friendRepository.getFriends(userId)
                .onSuccess { friends ->
                    _friendsState.value = UiState.Success(friends)
                }
                .onFailure { error ->
                    _friendsState.value = UiState.Error(
                        message = error.message ?: "Failed to load friends",
                        throwable = error
                    )
                }
        }
    }

    fun loadIncomingRequests() {
        screenModelScope.launch {
            _incomingRequestsState.value = UiState.Loading

            val userId = getCurrentUserId()
            if (userId == null) {
                _incomingRequestsState.value = UiState.Error("User not logged in")
                return@launch
            }

            friendRepository.getIncomingRequests(userId)
                .onSuccess { requests ->
                    _incomingRequestsState.value = UiState.Success(requests)
                }
                .onFailure { error ->
                    _incomingRequestsState.value = UiState.Error(
                        message = error.message ?: "Failed to load incoming requests",
                        throwable = error
                    )
                }
        }
    }

    fun loadOutgoingRequests() {
        screenModelScope.launch {
            _outgoingRequestsState.value = UiState.Loading

            val userId = getCurrentUserId()
            if (userId == null) {
                _outgoingRequestsState.value = UiState.Error("User not logged in")
                return@launch
            }

            friendRepository.getOutgoingRequests(userId)
                .onSuccess { requests ->
                    _outgoingRequestsState.value = UiState.Success(requests)
                }
                .onFailure { error ->
                    _outgoingRequestsState.value = UiState.Error(
                        message = error.message ?: "Failed to load outgoing requests",
                        throwable = error
                    )
                }
        }
    }

    fun searchFriends(query: String) {
        if (query.isBlank()) {
            _searchResultsState.value = UiState.Idle
            return
        }

        screenModelScope.launch {
            _searchResultsState.value = UiState.Loading

            val userId = getCurrentUserId()
            if (userId == null) {
                _searchResultsState.value = UiState.Error("User not logged in")
                return@launch
            }

            friendRepository.searchFriends(userId, query)
                .onSuccess { results ->
                    _searchResultsState.value = UiState.Success(results)
                }
                .onFailure { error ->
                    _searchResultsState.value = UiState.Error(
                        message = error.message ?: "Failed to search friends",
                        throwable = error
                    )
                }
        }
    }

    fun sendFriendRequest(recipientId: Long) {
        screenModelScope.launch {
            _sendRequestState.value = UiState.Loading

            val userId = getCurrentUserId()
            if (userId == null) {
                _sendRequestState.value = UiState.Error("User not logged in")
                return@launch
            }

            friendRepository.sendFriendRequest(userId, recipientId)
                .onSuccess { request ->
                    _sendRequestState.value = UiState.Success(request)
                }
                .onFailure { error ->
                    _sendRequestState.value = UiState.Error(
                        message = error.message ?: "Failed to send friend request",
                        throwable = error
                    )
                }
        }
    }

    fun acceptRequest(requestId: String) {
        screenModelScope.launch {
            _acceptRequestState.value = UiState.Loading

            val userId = getCurrentUserId()
            if (userId == null) {
                _acceptRequestState.value = UiState.Error("User not logged in")
                return@launch
            }

            friendRepository.acceptFriendRequest(requestId, userId)
                .onSuccess { request ->
                    _acceptRequestState.value = UiState.Success(request)
                    // Reload friends and requests
                    loadFriends()
                    loadIncomingRequests()
                }
                .onFailure { error ->
                    _acceptRequestState.value = UiState.Error(
                        message = error.message ?: "Failed to accept friend request",
                        throwable = error
                    )
                }
        }
    }

    fun rejectRequest(requestId: String) {
        screenModelScope.launch {
            _rejectRequestState.value = UiState.Loading

            val userId = getCurrentUserId()
            if (userId == null) {
                _rejectRequestState.value = UiState.Error("User not logged in")
                return@launch
            }

            friendRepository.rejectFriendRequest(requestId, userId)
                .onSuccess { request ->
                    _rejectRequestState.value = UiState.Success(request)
                    // Reload incoming requests
                    loadIncomingRequests()
                }
                .onFailure { error ->
                    _rejectRequestState.value = UiState.Error(
                        message = error.message ?: "Failed to reject friend request",
                        throwable = error
                    )
                }
        }
    }

    fun cancelRequest(requestId: String) {
        screenModelScope.launch {
            _cancelRequestState.value = UiState.Loading

            val userId = getCurrentUserId()
            if (userId == null) {
                _cancelRequestState.value = UiState.Error("User not logged in")
                return@launch
            }

            friendRepository.cancelFriendRequest(requestId, userId)
                .onSuccess {
                    _cancelRequestState.value = UiState.Success(Unit)
                    // Reload outgoing requests
                    loadOutgoingRequests()
                }
                .onFailure { error ->
                    _cancelRequestState.value = UiState.Error(
                        message = error.message ?: "Failed to cancel friend request",
                        throwable = error
                    )
                }
        }
    }

    fun removeFriend(friendId: Long) {
        screenModelScope.launch {
            _removeFriendState.value = UiState.Loading

            val userId = getCurrentUserId()
            if (userId == null) {
                _removeFriendState.value = UiState.Error("User not logged in")
                return@launch
            }

            friendRepository.removeFriend(userId, friendId)
                .onSuccess {
                    _removeFriendState.value = UiState.Success(Unit)
                    // Reload friends list
                    loadFriends()
                }
                .onFailure { error ->
                    _removeFriendState.value = UiState.Error(
                        message = error.message ?: "Failed to remove friend",
                        throwable = error
                    )
                }
        }
    }

    fun resetSearchResults() {
        _searchResultsState.value = UiState.Idle
    }

    fun resetSendRequestState() {
        _sendRequestState.value = UiState.Idle
    }

    fun resetAcceptRequestState() {
        _acceptRequestState.value = UiState.Idle
    }

    fun resetRejectRequestState() {
        _rejectRequestState.value = UiState.Idle
    }

    fun resetCancelRequestState() {
        _cancelRequestState.value = UiState.Idle
    }

    fun resetRemoveFriendState() {
        _removeFriendState.value = UiState.Idle
    }

    private fun getCurrentUserId(): Long? {
        // TODO: Implement proper user ID retrieval from TokenManager or User session
        // For now, we'll return a placeholder
        // You may need to add a method to TokenManager to store/retrieve user ID
        return 1L // Placeholder - replace with actual implementation
    }
}
