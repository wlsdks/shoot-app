package com.shoot.app.platform

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * 선택된 파일 정보
 */
data class SelectedFile(
    val name: String,
    val bytes: ByteArray,
    val mimeType: String,
    val size: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SelectedFile

        if (name != other.name) return false
        if (!bytes.contentEquals(other.bytes)) return false
        if (mimeType != other.mimeType) return false
        if (size != other.size) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + bytes.contentHashCode()
        result = 31 * result + mimeType.hashCode()
        result = 31 * result + size.hashCode()
        return result
    }
}

/**
 * 파일 선택기 타입
 */
enum class FilePickerType {
    IMAGE,      // 이미지만
    VIDEO,      // 비디오만
    AUDIO,      // 오디오만
    DOCUMENT,   // 문서 (PDF, DOC 등)
    ALL         // 모든 파일
}

/**
 * 파일 선택 결과
 */
sealed class FilePickerResult {
    data class Success(val file: SelectedFile) : FilePickerResult()
    data class Error(val message: String) : FilePickerResult()
    data object Cancelled : FilePickerResult()
}

/**
 * 파일 선택기 인터페이스
 * 플랫폼별로 구현 필요
 */
expect class FilePicker {
    /**
     * 파일 선택기를 열어 파일을 선택합니다.
     */
    suspend fun pickFile(type: FilePickerType = FilePickerType.ALL): FilePickerResult

    /**
     * 이미지를 선택합니다.
     */
    suspend fun pickImage(): FilePickerResult

    /**
     * 비디오를 선택합니다.
     */
    suspend fun pickVideo(): FilePickerResult

    /**
     * 카메라로 사진을 촬영합니다.
     */
    suspend fun takePhoto(): FilePickerResult

    /**
     * 카메라로 비디오를 녹화합니다.
     */
    suspend fun recordVideo(): FilePickerResult
}

/**
 * 파일 크기를 사람이 읽기 쉬운 형태로 변환
 */
fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
        else -> "${bytes / (1024 * 1024 * 1024)} GB"
    }
}

/**
 * MIME 타입에서 파일 타입 추론
 */
fun getMimeTypeCategory(mimeType: String): FilePickerType {
    return when {
        mimeType.startsWith("image/") -> FilePickerType.IMAGE
        mimeType.startsWith("video/") -> FilePickerType.VIDEO
        mimeType.startsWith("audio/") -> FilePickerType.AUDIO
        mimeType.startsWith("application/pdf") ||
        mimeType.startsWith("application/msword") ||
        mimeType.startsWith("application/vnd.") ||
        mimeType.startsWith("text/") -> FilePickerType.DOCUMENT
        else -> FilePickerType.ALL
    }
}
