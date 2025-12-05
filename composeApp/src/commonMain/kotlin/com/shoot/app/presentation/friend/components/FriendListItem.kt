package com.shoot.app.presentation.friend.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shoot.app.design.components.ShootCard
import com.shoot.app.domain.model.Friend
import com.shoot.app.domain.model.UserStatus

@Composable
fun FriendListItem(
    friend: Friend,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    ShootCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with status indicator
            Box {
                // Avatar placeholder
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Online status indicator
                if (friend.status == UserStatus.ONLINE) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .align(Alignment.BottomEnd)
                            .background(Color.Green, CircleShape)
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Friend info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = friend.nickname,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "@${friend.username}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Status text
                val statusText = when (friend.status) {
                    UserStatus.ONLINE -> "온라인"
                    UserStatus.AWAY -> "자리 비움"
                    UserStatus.OFFLINE -> "오프라인"
                }

                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodySmall,
                    color = when (friend.status) {
                        UserStatus.ONLINE -> Color.Green
                        UserStatus.AWAY -> MaterialTheme.colorScheme.tertiary
                        UserStatus.OFFLINE -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}
