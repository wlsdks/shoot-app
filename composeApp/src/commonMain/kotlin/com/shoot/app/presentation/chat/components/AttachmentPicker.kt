package com.shoot.app.presentation.chat.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.shoot.app.domain.model.Attachment
import com.shoot.app.platform.formatFileSize

enum class AttachmentType {
    IMAGE,
    VIDEO,
    AUDIO,
    CAMERA,
    FILE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttachmentPickerBottomSheet(
    onDismiss: () -> Unit,
    onTypeSelected: (AttachmentType) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "첨부하기",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AttachmentOption(
                    icon = Icons.Default.Image,
                    label = "사진",
                    onClick = { onTypeSelected(AttachmentType.IMAGE) }
                )
                AttachmentOption(
                    icon = Icons.Default.VideoLibrary,
                    label = "동영상",
                    onClick = { onTypeSelected(AttachmentType.VIDEO) }
                )
                AttachmentOption(
                    icon = Icons.Default.Audiotrack,
                    label = "오디오",
                    onClick = { onTypeSelected(AttachmentType.AUDIO) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AttachmentOption(
                    icon = Icons.Default.PhotoCamera,
                    label = "카메라",
                    onClick = { onTypeSelected(AttachmentType.CAMERA) }
                )
                AttachmentOption(
                    icon = Icons.Default.InsertDriveFile,
                    label = "파일",
                    onClick = { onTypeSelected(AttachmentType.FILE) }
                )
                // 빈 공간 (정렬용)
                Spacer(modifier = Modifier.size(72.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AttachmentOption(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.padding(16.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

/**
 * 첨부된 파일 미리보기 컴포넌트
 */
@Composable
fun AttachmentPreview(
    attachment: Attachment,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 파일 아이콘
            Icon(
                imageVector = getFileIcon(attachment.contentType),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 파일 정보
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = attachment.filename,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
                Text(
                    text = formatFileSize(attachment.size),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 제거 버튼
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "제거",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * 파일 업로드 진행률 표시
 */
@Composable
fun UploadProgressIndicator(
    fileName: String,
    progress: Float, // 0.0 ~ 1.0
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = "업로드 중: $fileName",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            androidx.compose.material3.LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * MIME 타입에 따른 아이콘 반환
 */
private fun getFileIcon(mimeType: String): ImageVector {
    return when {
        mimeType.startsWith("image/") -> Icons.Default.Image
        mimeType.startsWith("video/") -> Icons.Default.VideoLibrary
        mimeType.startsWith("audio/") -> Icons.Default.Audiotrack
        else -> Icons.Default.InsertDriveFile
    }
}
