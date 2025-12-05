package com.shoot.app.presentation.chat.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 타이핑 인디케이터 컴포넌트
 * 다른 사용자가 메시지를 입력 중일 때 표시
 */
@Composable
fun TypingIndicator(
    typingUserNames: List<String>,
    modifier: Modifier = Modifier
) {
    if (typingUserNames.isEmpty()) return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Typing text
        val displayText = when {
            typingUserNames.size == 1 -> "${typingUserNames[0]}님이 입력 중"
            typingUserNames.size == 2 -> "${typingUserNames[0]}님, ${typingUserNames[1]}님이 입력 중"
            else -> "${typingUserNames[0]}님 외 ${typingUserNames.size - 1}명이 입력 중"
        }

        Text(
            text = displayText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontSize = 12.sp
        )

        // Animated dots
        AnimatedTypingDots()
    }
}

@Composable
private fun AnimatedTypingDots() {
    val infiniteTransition = rememberInfiniteTransition()

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 600,
                        delayMillis = index * 200,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ), label = "typing_dot_$index"
            )

            Box(
                modifier = Modifier
                    .size(6.dp)
                    .alpha(alpha)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            )
        }
    }
}
