package com.shoot.app.presentation.chat.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 메시지 편집 다이얼로그
 */
@Composable
fun EditMessageDialog(
    currentText: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var editedText by remember { mutableStateOf(currentText) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("메시지 편집") },
        text = {
            TextField(
                value = editedText,
                onValueChange = { editedText = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("메시지를 입력하세요") },
                maxLines = 5
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (editedText.isNotBlank() && editedText != currentText) {
                        onConfirm(editedText)
                    }
                },
                enabled = editedText.isNotBlank() && editedText != currentText
            ) {
                Text("저장")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

/**
 * 메시지 삭제 확인 다이얼로그
 */
@Composable
fun DeleteMessageDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("메시지 삭제") },
        text = { Text("이 메시지를 삭제하시겠습니까?") },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("삭제")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}
