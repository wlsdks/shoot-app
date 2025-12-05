package com.shoot.app.presentation.chatroom

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.shoot.app.data.local.TokenManager
import com.shoot.app.data.repository.ChatRoomRepository
import com.shoot.app.domain.model.ChatRoom
import com.shoot.app.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatRoomViewModel(
    private val chatRoomRepository: ChatRoomRepository,
    private val tokenManager: TokenManager
) : ScreenModel {

    private val _chatRoomsState = MutableStateFlow<UiState<List<ChatRoom>>>(UiState.Idle)
    val chatRoomsState: StateFlow<UiState<List<ChatRoom>>> = _chatRoomsState.asStateFlow()

    private val _createDirectChatState = MutableStateFlow<UiState<ChatRoom>>(UiState.Idle)
    val createDirectChatState: StateFlow<UiState<ChatRoom>> = _createDirectChatState.asStateFlow()

    private val _createGroupChatState = MutableStateFlow<UiState<ChatRoom>>(UiState.Idle)
    val createGroupChatState: StateFlow<UiState<ChatRoom>> = _createGroupChatState.asStateFlow()

    private val _exitChatRoomState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val exitChatRoomState: StateFlow<UiState<Unit>> = _exitChatRoomState.asStateFlow()

    private val _toggleFavoriteState = MutableStateFlow<UiState<ChatRoom>>(UiState.Idle)
    val toggleFavoriteState: StateFlow<UiState<ChatRoom>> = _toggleFavoriteState.asStateFlow()

    private val _searchResultsState = MutableStateFlow<UiState<List<ChatRoom>>>(UiState.Idle)
    val searchResultsState: StateFlow<UiState<List<ChatRoom>>> = _searchResultsState.asStateFlow()

    fun loadChatRooms() {
        screenModelScope.launch {
            _chatRoomsState.value = UiState.Loading

            val userId = getCurrentUserId()
            if (userId == null) {
                _chatRoomsState.value = UiState.Error("User not logged in")
                return@launch
            }

            chatRoomRepository.getChatRooms(userId)
                .onSuccess { chatRooms ->
                    _chatRoomsState.value = UiState.Success(chatRooms)
                }
                .onFailure { error ->
                    _chatRoomsState.value = UiState.Error(
                        message = error.message ?: "Failed to load chat rooms",
                        throwable = error
                    )
                }
        }
    }

    fun createDirectChat(recipientId: Long) {
        screenModelScope.launch {
            _createDirectChatState.value = UiState.Loading

            val userId = getCurrentUserId()
            if (userId == null) {
                _createDirectChatState.value = UiState.Error("User not logged in")
                return@launch
            }

            chatRoomRepository.createDirectChat(userId, recipientId)
                .onSuccess { chatRoom ->
                    _createDirectChatState.value = UiState.Success(chatRoom)
                    // Reload chat rooms to include the new one
                    loadChatRooms()
                }
                .onFailure { error ->
                    _createDirectChatState.value = UiState.Error(
                        message = error.message ?: "Failed to create direct chat",
                        throwable = error
                    )
                }
        }
    }

    fun createGroupChat(participantIds: List<Long>, title: String, description: String? = null) {
        screenModelScope.launch {
            _createGroupChatState.value = UiState.Loading

            val userId = getCurrentUserId()
            if (userId == null) {
                _createGroupChatState.value = UiState.Error("User not logged in")
                return@launch
            }

            chatRoomRepository.createGroupChat(userId, participantIds, title, description)
                .onSuccess { chatRoom ->
                    _createGroupChatState.value = UiState.Success(chatRoom)
                    // Reload chat rooms to include the new one
                    loadChatRooms()
                }
                .onFailure { error ->
                    _createGroupChatState.value = UiState.Error(
                        message = error.message ?: "Failed to create group chat",
                        throwable = error
                    )
                }
        }
    }

    fun exitChatRoom(roomId: Long) {
        screenModelScope.launch {
            _exitChatRoomState.value = UiState.Loading

            val userId = getCurrentUserId()
            if (userId == null) {
                _exitChatRoomState.value = UiState.Error("User not logged in")
                return@launch
            }

            chatRoomRepository.exitChatRoom(roomId, userId)
                .onSuccess {
                    _exitChatRoomState.value = UiState.Success(Unit)
                    // Reload chat rooms to reflect the change
                    loadChatRooms()
                }
                .onFailure { error ->
                    _exitChatRoomState.value = UiState.Error(
                        message = error.message ?: "Failed to exit chat room",
                        throwable = error
                    )
                }
        }
    }

    fun toggleFavorite(roomId: Long, isFavorite: Boolean) {
        screenModelScope.launch {
            _toggleFavoriteState.value = UiState.Loading

            val userId = getCurrentUserId()
            if (userId == null) {
                _toggleFavoriteState.value = UiState.Error("User not logged in")
                return@launch
            }

            chatRoomRepository.toggleFavorite(userId, roomId, isFavorite)
                .onSuccess { chatRoom ->
                    _toggleFavoriteState.value = UiState.Success(chatRoom)
                    // Update the chat rooms list with the new favorite status
                    loadChatRooms()
                }
                .onFailure { error ->
                    _toggleFavoriteState.value = UiState.Error(
                        message = error.message ?: "Failed to toggle favorite",
                        throwable = error
                    )
                }
        }
    }

    fun searchChatRooms(query: String) {
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

            chatRoomRepository.searchChatRooms(userId, query)
                .onSuccess { results ->
                    _searchResultsState.value = UiState.Success(results)
                }
                .onFailure { error ->
                    _searchResultsState.value = UiState.Error(
                        message = error.message ?: "Failed to search chat rooms",
                        throwable = error
                    )
                }
        }
    }

    fun resetCreateDirectChatState() {
        _createDirectChatState.value = UiState.Idle
    }

    fun resetCreateGroupChatState() {
        _createGroupChatState.value = UiState.Idle
    }

    fun resetExitChatRoomState() {
        _exitChatRoomState.value = UiState.Idle
    }

    fun resetToggleFavoriteState() {
        _toggleFavoriteState.value = UiState.Idle
    }

    fun resetSearchResults() {
        _searchResultsState.value = UiState.Idle
    }

    private fun getCurrentUserId(): Long? {
        // TODO: Implement proper user ID retrieval from TokenManager or User session
        // For now, we'll return a placeholder
        // You may need to add a method to TokenManager to store/retrieve user ID
        return 1L // Placeholder - replace with actual implementation
    }
}
