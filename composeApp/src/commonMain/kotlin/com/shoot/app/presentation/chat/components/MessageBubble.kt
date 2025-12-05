package com.shoot.app.presentation.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shoot.app.design.theme.*
import com.shoot.app.domain.model.Message
import com.shoot.app.domain.model.MessageStatus

@Composable
fun MessageBubble(
    message: Message,
    isCurrentUser: Boolean,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
        ) {
            // Message bubble
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isCurrentUser) 16.dp else 4.dp,
                    bottomEnd = if (isCurrentUser) 4.dp else 16.dp
                ),
                color = if (isCurrentUser) {
                    MessageBubbleSent
                } else {
                    MessageBubbleReceived
                },
                tonalElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    // Message text
                    Text(
                        text = message.content.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isCurrentUser) Color.White else MaterialTheme.colorScheme.onSurface
                    )

                    // Show edit indicator if edited
                    if (message.content.isEdited) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "편집됨",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isCurrentUser) {
                                Color.White.copy(alpha = 0.7f)
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            },
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Timestamp and status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
            ) {
                // Timestamp
                Text(
                    text = formatTimestamp(message.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontSize = 10.sp
                )

                // Status indicator (only for current user's messages)
                if (isCurrentUser) {
                    Spacer(modifier = Modifier.width(4.dp))
                    MessageStatusIndicator(
                        status = message.status,
                        onRetry = onRetry
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageStatusIndicator(
    status: MessageStatus,
    onRetry: (() -> Unit)?
) {
    when (status) {
        MessageStatus.SENDING -> {
            CircularProgressIndicator(
                modifier = Modifier.size(12.dp),
                strokeWidth = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        MessageStatus.SENT_TO_KAFKA, MessageStatus.PROCESSING -> {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Sent",
                modifier = Modifier.size(12.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        MessageStatus.SAVED -> {
            // Double check icon for delivered
            Row(horizontalArrangement = Arrangement.spacedBy((-4).dp)) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Delivered",
                    modifier = Modifier.size(12.dp),
                    tint = Success
                )
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Delivered",
                    modifier = Modifier.size(12.dp),
                    tint = Success
                )
            }
        }
        MessageStatus.FAILED -> {
            IconButton(
                onClick = { onRetry?.invoke() },
                modifier = Modifier.size(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Failed - Retry",
                    tint = androidx.compose.ui.graphics.Color.Red,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

/**
 * 타임스탬프 포맷팅 (HH:mm)
 */
private fun formatTimestamp(timestamp: String): String {
    return try {
        // Simple formatting - just extract time from ISO string
        // Expected format: 2025-12-05T10:30:00Z
        if (timestamp.contains("T")) {
            val timePart = timestamp.substringAfter("T").substringBefore("Z").substringBefore(".")
            val parts = timePart.split(":")
            if (parts.size >= 2) {
                "${parts[0]}:${parts[1]}"
            } else {
                ""
            }
        } else {
            ""
        }
    } catch (e: Exception) {
        ""
    }
}
