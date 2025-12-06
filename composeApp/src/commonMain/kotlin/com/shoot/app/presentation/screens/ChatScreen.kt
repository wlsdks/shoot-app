package com.shoot.app.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.shoot.app.data.local.TokenManager
import com.shoot.app.data.repository.MessageRepository
import com.shoot.app.data.websocket.ConnectionState
import com.shoot.app.data.websocket.WebSocketClient
import com.shoot.app.design.components.ShootLoadingIndicator
import com.shoot.app.design.components.ShootTextField
import com.shoot.app.domain.model.ChatRoom
import com.shoot.app.presentation.common.UiState
import com.shoot.app.presentation.chat.ChatViewModel
import com.shoot.app.presentation.chat.components.MessageBubble
import com.shoot.app.presentation.chat.components.EditMessageDialog
import com.shoot.app.presentation.chat.components.DeleteMessageDialog
import com.shoot.app.presentation.chat.components.TypingIndicator
import com.shoot.app.domain.model.Message
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

data class ChatScreen(
    val chatRoom: ChatRoom,
    val currentUserId: Long
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        // Inject dependencies
        val messageRepository: MessageRepository = koinInject()
        val webSocketClient: WebSocketClient = koinInject()
        val tokenManager: TokenManager = koinInject()

        // Create ViewModel
        val viewModel = remember {
            ChatViewModel(
                roomId = chatRoom.roomId,
                currentUserId = currentUserId,
                messageRepository = messageRepository,
                webSocketClient = webSocketClient,
                tokenManager = tokenManager
            )
        }

        val messages by viewModel.messages.collectAsState()
        val messagesState by viewModel.messagesState.collectAsState()
        val connectionState by viewModel.connectionState.collectAsState()
        val typingUsers by viewModel.typingUsers.collectAsState()

        // Search states
        val isSearchMode by viewModel.isSearchMode.collectAsState()
        val searchQuery by viewModel.searchQuery.collectAsState()
        val searchResults by viewModel.searchResults.collectAsState()
        val searchState by viewModel.searchState.collectAsState()

        var messageText by remember { mutableStateOf("") }
        var wasTyping by remember { mutableStateOf(false) }
        val listState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()

        // Dialog states
        var messageToEdit by remember { mutableStateOf<Message?>(null) }
        var messageToDelete by remember { mutableStateOf<Message?>(null) }

        // Auto-scroll to bottom when new message arrives
        LaunchedEffect(messages.size) {
            if (messages.isNotEmpty() && !isSearchMode) {
                // 현재 스크롤 위치가 거의 하단에 있을 때만 자동 스크롤
                val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                val isNearBottom = lastVisibleItem?.let {
                    it.index >= messages.size - 3
                } ?: true

                if (isNearBottom) {
                    coroutineScope.launch {
                        listState.animateScrollToItem(messages.size - 1)
                    }
                }
            }
        }

        Scaffold(
            topBar = {
                if (isSearchMode) {
                    SearchTopBar(
                        searchQuery = searchQuery,
                        onQueryChange = { viewModel.updateSearchQuery(it) },
                        onClose = { viewModel.exitSearchMode() }
                    )
                } else {
                    ChatTopBar(
                        title = chatRoom.title,
                        subtitle = when (connectionState) {
                            is ConnectionState.Connected -> "온라인"
                            is ConnectionState.Connecting -> "연결 중..."
                            is ConnectionState.Disconnected -> "오프라인"
                            is ConnectionState.Error -> "연결 오류"
                        },
                        onBackClick = { navigator.pop() },
                        onSearchClick = { viewModel.enterSearchMode() }
                    )
                }
            },
            bottomBar = {
                if (!isSearchMode) {
                    ChatInputBar(
                        value = messageText,
                        onValueChange = { newText ->
                            messageText = newText

                            // 타이핑 이벤트 전송
                            val isTyping = newText.isNotBlank()
                            if (isTyping != wasTyping) {
                                viewModel.sendTypingEvent(isTyping)
                                wasTyping = isTyping
                            }
                        },
                        onSendClick = {
                            if (messageText.isNotBlank()) {
                                viewModel.sendMessage(messageText)
                                messageText = ""
                                // 전송 후 타이핑 종료 이벤트
                                viewModel.sendTypingEvent(false)
                                wasTyping = false
                            }
                        }
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (isSearchMode) {
                    // Search results
                    SearchResultsContent(
                        searchState = searchState,
                        searchResults = searchResults,
                        searchQuery = searchQuery,
                        onMessageClick = { message ->
                            // 검색 모드 종료 후 해당 메시지로 이동
                            viewModel.exitSearchMode()
                            val index = viewModel.scrollToMessage(message.id)
                            if (index >= 0) {
                                coroutineScope.launch {
                                    listState.animateScrollToItem(index)
                                }
                            }
                        }
                    )
                } else {
                    // Normal chat content
                    when (messagesState) {
                        is UiState.Loading -> {
                            ShootLoadingIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        is UiState.Error -> {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = (messagesState as UiState.Error).message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = { /* Retry */ }) {
                                    Text("다시 시도")
                                }
                            }
                        }
                        is UiState.Success, UiState.Idle -> {
                            if (messages.isEmpty()) {
                                // Empty state
                                Text(
                                    text = "메시지를 입력하여 대화를 시작하세요",
                                    modifier = Modifier.align(Alignment.Center),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            } else {
                                // Messages list
                                Column(modifier = Modifier.fillMaxSize()) {
                                    LazyColumn(
                                        state = listState,
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(vertical = 8.dp)
                                    ) {
                                        items(
                                            items = messages,
                                            key = { it.tempId ?: it.id }
                                        ) { message ->
                                            MessageBubble(
                                                message = message,
                                                isCurrentUser = message.senderId == currentUserId,
                                                totalParticipants = chatRoom.participants.size,
                                                onRetry = { viewModel.retryMessage(message) },
                                                onEdit = {
                                                    messageToEdit = message
                                                },
                                                onDelete = {
                                                    messageToDelete = message
                                                },
                                                onReact = { reactionType ->
                                                    viewModel.toggleReaction(message.id, reactionType)
                                                }
                                            )
                                        }
                                    }

                                    // Typing indicator
                                    if (typingUsers.isNotEmpty()) {
                                        TypingIndicator(typingUserNames = typingUsers)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Edit message dialog
        messageToEdit?.let { message ->
            EditMessageDialog(
                currentText = message.content.text,
                onDismiss = { messageToEdit = null },
                onConfirm = { newText ->
                    viewModel.editMessage(message.id, newText)
                    messageToEdit = null
                }
            )
        }

        // Delete message dialog
        messageToDelete?.let { message ->
            DeleteMessageDialog(
                onDismiss = { messageToDelete = null },
                onConfirm = {
                    viewModel.deleteMessage(message.id)
                    messageToDelete = null
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatTopBar(
    title: String,
    subtitle: String,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로 가기"
                )
            }
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "메시지 검색"
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit
) {
    TopAppBar(
        title = {
            TextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                placeholder = { Text("메시지 검색...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "검색 닫기"
                )
            }
        },
        actions = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "검색어 지우기"
                    )
                }
            }
        }
    )
}

@Composable
private fun SearchResultsContent(
    searchState: UiState<List<Message>>,
    searchResults: List<Message>,
    searchQuery: String,
    onMessageClick: (Message) -> Unit
) {
    when (searchState) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is UiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (searchState as UiState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        is UiState.Success -> {
            if (searchResults.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "\"$searchQuery\"에 대한 검색 결과가 없습니다",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    item {
                        Text(
                            text = "${searchResults.size}개의 검색 결과",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    items(searchResults) { message ->
                        SearchResultItem(
                            message = message,
                            searchQuery = searchQuery,
                            onClick = { onMessageClick(message) }
                        )
                    }
                }
            }
        }
        is UiState.Idle -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "검색어를 입력하세요 (2자 이상)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchResultItem(
    message: Message,
    searchQuery: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = message.senderNickname ?: "사용자 ${message.senderId}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = formatMessageTime(message.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = message.content.text,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
}

private fun formatMessageTime(timestamp: String?): String {
    if (timestamp == null) return ""
    return try {
        // ISO 8601 형식에서 날짜와 시간 추출
        val parts = timestamp.split("T")
        if (parts.size >= 2) {
            val datePart = parts[0]
            val timePart = parts[1].substringBefore(".")
            val timeComponents = timePart.split(":")
            if (timeComponents.size >= 2) {
                "$datePart ${timeComponents[0]}:${timeComponents[1]}"
            } else {
                datePart
            }
        } else {
            timestamp
        }
    } catch (e: Exception) {
        timestamp
    }
}

@Composable
private fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Surface(
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("메시지 입력...") },
                maxLines = 4,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onSendClick,
                enabled = value.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "전송",
                    tint = if (value.isNotBlank()) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    }
                )
            }
        }
    }
}
