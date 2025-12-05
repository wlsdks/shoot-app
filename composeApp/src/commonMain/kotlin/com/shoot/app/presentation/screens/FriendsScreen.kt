package com.shoot.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.shoot.app.design.components.ButtonSize
import com.shoot.app.design.components.ShootButton
import com.shoot.app.design.components.ShootCard
import com.shoot.app.design.components.ShootOutlinedButton
import com.shoot.app.design.components.ShootTextField
import com.shoot.app.domain.model.Friend
import com.shoot.app.domain.model.FriendRequest
import com.shoot.app.presentation.common.UiState
import com.shoot.app.presentation.friend.FriendViewModel
import com.shoot.app.presentation.friend.components.AddFriendDialog
import com.shoot.app.presentation.friend.components.FriendListItem

class FriendsScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel: FriendViewModel = koinScreenModel()
        FriendsScreenContent(viewModel = viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FriendsScreenContent(
    viewModel: FriendViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddFriendDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val friendsState by viewModel.friendsState.collectAsState()
    val incomingRequestsState by viewModel.incomingRequestsState.collectAsState()
    val outgoingRequestsState by viewModel.outgoingRequestsState.collectAsState()

    // Load data when screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.loadFriends()
        viewModel.loadIncomingRequests()
        viewModel.loadOutgoingRequests()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("친구") }
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showAddFriendDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "친구 추가"
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("친구 목록") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("받은 요청") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("보낸 요청") }
                )
            }

            // Search bar (only for friends list)
            if (selectedTab == 0) {
                ShootTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "친구 검색",
                    leadingIcon = Icons.Default.Search,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }

            // Content based on selected tab
            when (selectedTab) {
                0 -> FriendsListTab(
                    friendsState = friendsState,
                    searchQuery = searchQuery,
                    onRemoveFriend = { viewModel.removeFriend(it) },
                    modifier = Modifier.weight(1f)
                )
                1 -> IncomingRequestsTab(
                    requestsState = incomingRequestsState,
                    onAccept = { viewModel.acceptRequest(it) },
                    onReject = { viewModel.rejectRequest(it) },
                    modifier = Modifier.weight(1f)
                )
                2 -> OutgoingRequestsTab(
                    requestsState = outgoingRequestsState,
                    onCancel = { viewModel.cancelRequest(it) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    // Add Friend Dialog
    if (showAddFriendDialog) {
        AddFriendDialog(
            viewModel = viewModel,
            onDismiss = { showAddFriendDialog = false }
        )
    }
}

@Composable
private fun FriendsListTab(
    friendsState: UiState<List<Friend>>,
    searchQuery: String,
    onRemoveFriend: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    when (friendsState) {
        is UiState.Idle -> {
            EmptyState("친구 목록을 불러오는 중...", modifier)
        }

        is UiState.Loading -> {
            LoadingState(modifier)
        }

        is UiState.Success -> {
            val filteredFriends = if (searchQuery.isBlank()) {
                friendsState.data
            } else {
                friendsState.data.filter {
                    it.nickname.contains(searchQuery, ignoreCase = true) ||
                    it.username.contains(searchQuery, ignoreCase = true)
                }
            }

            if (filteredFriends.isEmpty()) {
                EmptyState(
                    if (searchQuery.isBlank()) "친구가 없습니다" else "검색 결과가 없습니다",
                    modifier
                )
            } else {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredFriends) { friend ->
                        FriendListItem(
                            friend = friend,
                            onClick = { /* TODO: Navigate to friend profile */ }
                        )
                    }
                }
            }
        }

        is UiState.Error -> {
            ErrorState(friendsState.message, modifier)
        }
    }
}

@Composable
private fun IncomingRequestsTab(
    requestsState: UiState<List<FriendRequest>>,
    onAccept: (String) -> Unit,
    onReject: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (requestsState) {
        is UiState.Idle -> {
            EmptyState("받은 요청을 불러오는 중...", modifier)
        }

        is UiState.Loading -> {
            LoadingState(modifier)
        }

        is UiState.Success -> {
            if (requestsState.data.isEmpty()) {
                EmptyState("받은 친구 요청이 없습니다", modifier)
            } else {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(requestsState.data) { request ->
                        FriendRequestItem(
                            request = request,
                            onAccept = { onAccept(request.requestId) },
                            onReject = { onReject(request.requestId) }
                        )
                    }
                }
            }
        }

        is UiState.Error -> {
            ErrorState(requestsState.message, modifier)
        }
    }
}

@Composable
private fun OutgoingRequestsTab(
    requestsState: UiState<List<FriendRequest>>,
    onCancel: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (requestsState) {
        is UiState.Idle -> {
            EmptyState("보낸 요청을 불러오는 중...", modifier)
        }

        is UiState.Loading -> {
            LoadingState(modifier)
        }

        is UiState.Success -> {
            if (requestsState.data.isEmpty()) {
                EmptyState("보낸 친구 요청이 없습니다", modifier)
            } else {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(requestsState.data) { request ->
                        OutgoingRequestItem(
                            request = request,
                            onCancel = { onCancel(request.requestId) }
                        )
                    }
                }
            }
        }

        is UiState.Error -> {
            ErrorState(requestsState.message, modifier)
        }
    }
}

@Composable
private fun FriendRequestItem(
    request: FriendRequest,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier
) {
    ShootCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = request.senderNickname,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "@${request.senderUsername}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (request.message != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = request.message,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ShootButton(
                    text = "수락",
                    onClick = onAccept,
                    modifier = Modifier.weight(1f),
                    size = ButtonSize.Small
                )
                ShootOutlinedButton(
                    text = "거절",
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    size = ButtonSize.Small
                )
            }
        }
    }
}

@Composable
private fun OutgoingRequestItem(
    request: FriendRequest,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    ShootCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = request.senderNickname,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "@${request.senderUsername}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "대기 중",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            ShootOutlinedButton(
                text = "취소",
                onClick = onCancel,
                size = ButtonSize.Small
            )
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorState(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "오류 발생",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
