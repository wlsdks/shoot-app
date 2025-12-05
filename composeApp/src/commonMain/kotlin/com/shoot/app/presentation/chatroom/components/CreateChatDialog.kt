package com.shoot.app.presentation.chatroom.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shoot.app.design.components.ShootTextField
import com.shoot.app.domain.model.Friend

@Composable
fun CreateChatDialog(
    friends: List<Friend>,
    onDismiss: () -> Unit,
    onCreateDirectChat: (Long) -> Unit,
    onCreateGroupChat: (List<Long>, String, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFriends by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var groupName by remember { mutableStateOf("") }
    var groupDescription by remember { mutableStateOf("") }

    val filteredFriends = remember(friends, searchQuery) {
        if (searchQuery.isBlank()) {
            friends
        } else {
            friends.filter { friend ->
                friend.nickname.contains(searchQuery, ignoreCase = true) ||
                friend.username.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "새 채팅",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tab selector
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("1:1 채팅") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("그룹 채팅") }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Search bar
                ShootTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = "친구 검색",
                    leadingIcon = Icons.Default.Search,
                    imeAction = ImeAction.Search
                )

                // Group chat specific fields
                if (selectedTab == 1) {
                    ShootTextField(
                        value = groupName,
                        onValueChange = { groupName = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = "그룹 이름",
                        placeholder = "그룹 이름을 입력하세요"
                    )

                    ShootTextField(
                        value = groupDescription,
                        onValueChange = { groupDescription = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = "그룹 설명 (선택)",
                        placeholder = "그룹 설명을 입력하세요",
                        maxLines = 3,
                        singleLine = false
                    )
                }

                // Friends list
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    if (filteredFriends.isEmpty()) {
                        Text(
                            text = if (searchQuery.isBlank()) "친구가 없습니다" else "검색 결과가 없습니다",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredFriends) { friend ->
                                FriendItem(
                                    friend = friend,
                                    isSelected = selectedFriends.contains(friend.userId),
                                    isGroupMode = selectedTab == 1,
                                    onClick = {
                                        if (selectedTab == 0) {
                                            // Direct chat - create immediately
                                            onCreateDirectChat(friend.userId)
                                        } else {
                                            // Group chat - toggle selection
                                            selectedFriends = if (selectedFriends.contains(friend.userId)) {
                                                selectedFriends - friend.userId
                                            } else {
                                                selectedFriends + friend.userId
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (selectedTab == 1) {
                TextButton(
                    onClick = {
                        if (selectedFriends.isNotEmpty() && groupName.isNotBlank()) {
                            onCreateGroupChat(
                                selectedFriends.toList(),
                                groupName,
                                groupDescription.takeIf { it.isNotBlank() }
                            )
                        }
                    },
                    enabled = selectedFriends.isNotEmpty() && groupName.isNotBlank()
                ) {
                    Text("생성")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        },
        shape = MaterialTheme.shapes.large
    )
}

@Composable
private fun FriendItem(
    friend: Friend,
    isSelected: Boolean,
    isGroupMode: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                else MaterialTheme.colorScheme.surface
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(20.dp)
            )
        }

        // Friend info
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = friend.nickname,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "@${friend.username}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Selection indicator (for group mode)
        if (isGroupMode) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
