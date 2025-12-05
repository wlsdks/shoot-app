package com.shoot.app.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.shoot.app.data.local.TokenManager
import com.shoot.app.data.repository.MessageRepository
import com.shoot.app.data.websocket.ConnectionState
import com.shoot.app.data.websocket.WebSocketClient
import com.shoot.app.design.components.ShootLoadingIndicator
import com.shoot.app.domain.model.ChatRoom
import com.shoot.app.presentation.common.UiState
import com.shoot.app.presentation.chat.ChatViewModel
import com.shoot.app.presentation.chat.components.MessageBubble
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

        var messageText by remember { mutableStateOf("") }
        val listState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()

        // Auto-scroll to bottom when new message arrives
        LaunchedEffect(messages.size) {
            if (messages.isNotEmpty()) {
                coroutineScope.launch {
                    listState.animateScrollToItem(messages.size - 1)
                }
            }
        }

        Scaffold(
            topBar = {
                ChatTopBar(
                    title = chatRoom.title,
                    subtitle = when (connectionState) {
                        is ConnectionState.Connected -> "온라인"
                        is ConnectionState.Connecting -> "연결 중..."
                        is ConnectionState.Disconnected -> "오프라인"
                        is ConnectionState.Error -> "연결 오류"
                    },
                    onBackClick = { navigator.pop() }
                )
            },
            bottomBar = {
                ChatInputBar(
                    value = messageText,
                    onValueChange = { messageText = it },
                    onSendClick = {
                        if (messageText.isNotBlank()) {
                            viewModel.sendMessage(messageText)
                            messageText = ""
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
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
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                items(
                                    items = messages,
                                    key = { it.tempId ?: it.id }
                                ) { message ->
                                    MessageBubble(
                                        message = message,
                                        isCurrentUser = message.senderId == currentUserId,
                                        onRetry = { viewModel.retryMessage(message) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatTopBar(
    title: String,
    subtitle: String,
    onBackClick: () -> Unit
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
        }
    )
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
