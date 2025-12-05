package com.shoot.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.shoot.app.design.components.ShootErrorDialog
import com.shoot.app.design.components.ShootLoadingIndicator
import com.shoot.app.design.components.ShootTextField
import com.shoot.app.domain.model.ChatRoom
import com.shoot.app.presentation.chatroom.ChatRoomViewModel
import com.shoot.app.presentation.chatroom.components.ChatRoomListItem
import com.shoot.app.presentation.chatroom.components.CreateChatDialog
import com.shoot.app.presentation.common.UiState
import com.shoot.app.presentation.friend.FriendViewModel

class ChatListScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val chatRoomViewModel = koinScreenModel<ChatRoomViewModel>()
        val friendViewModel = koinScreenModel<FriendViewModel>()

        val chatRoomsState by chatRoomViewModel.chatRoomsState.collectAsState()
        val searchResultsState by chatRoomViewModel.searchResultsState.collectAsState()
        val friendsState by friendViewModel.friendsState.collectAsState()

        var searchQuery by remember { mutableStateOf("") }
        var showFavoritesOnly by remember { mutableStateOf(false) }
        var showCreateDialog by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        // Load data on first composition
        LaunchedEffect(Unit) {
            chatRoomViewModel.loadChatRooms()
            friendViewModel.loadFriends()
        }

        // Handle search
        LaunchedEffect(searchQuery) {
            if (searchQuery.isNotBlank()) {
                chatRoomViewModel.searchChatRooms(searchQuery)
            } else {
                chatRoomViewModel.resetSearchResults()
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "채팅",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "새 채팅"
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                // Search bar
                ShootTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    placeholder = "채팅방 검색",
                    leadingIcon = Icons.Default.Search,
                    imeAction = ImeAction.Search
                )

                // Filter chip for favorites
                FilterChip(
                    selected = showFavoritesOnly,
                    onClick = { showFavoritesOnly = !showFavoritesOnly },
                    label = { Text("즐겨찾기") },
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Chat rooms list
                val displayState = if (searchQuery.isNotBlank()) searchResultsState else chatRoomsState

                when (displayState) {
                    is UiState.Idle -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "채팅방이 없습니다",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    is UiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            ShootLoadingIndicator()
                        }
                    }

                    is UiState.Success -> {
                        val chatRooms = displayState.data
                        val filteredChatRooms = if (showFavoritesOnly) {
                            chatRooms.filter { it.isFavorite }
                        } else {
                            chatRooms
                        }

                        if (filteredChatRooms.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (showFavoritesOnly) {
                                        "즐겨찾기한 채팅방이 없습니다"
                                    } else if (searchQuery.isNotBlank()) {
                                        "검색 결과가 없습니다"
                                    } else {
                                        "채팅방이 없습니다"
                                    },
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(filteredChatRooms) { chatRoom ->
                                    ChatRoomListItem(
                                        chatRoom = chatRoom,
                                        onClick = {
                                            // TODO: Navigate to chat detail screen
                                            // navigator.push(ChatDetailScreen(chatRoom.roomId))
                                        },
                                        onToggleFavorite = {
                                            chatRoomViewModel.toggleFavorite(
                                                chatRoom.roomId,
                                                !chatRoom.isFavorite
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }

                    is UiState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = displayState.message,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = "다시 시도해 주세요",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Create chat dialog
        if (showCreateDialog) {
            val friends = when (val state = friendsState) {
                is UiState.Success -> state.data
                else -> emptyList()
            }

            CreateChatDialog(
                friends = friends,
                onDismiss = { showCreateDialog = false },
                onCreateDirectChat = { friendId ->
                    chatRoomViewModel.createDirectChat(friendId)
                    showCreateDialog = false
                },
                onCreateGroupChat = { participantIds, title, description ->
                    chatRoomViewModel.createGroupChat(participantIds, title, description)
                    showCreateDialog = false
                }
            )
        }

        // Error dialog
        errorMessage?.let { message ->
            ShootErrorDialog(
                message = message,
                onDismiss = { errorMessage = null }
            )
        }
    }
}
