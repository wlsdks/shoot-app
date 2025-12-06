package com.shoot.app.data.repository

import com.shoot.app.data.remote.api.FileApiService
import com.shoot.app.data.remote.dto.file.toAttachment
import com.shoot.app.domain.model.Attachment
import com.shoot.app.utils.safeApiCall

interface FileRepository {
    suspend fun uploadFile(
        fileBytes: ByteArray,
        fileName: String,
        contentType: String,
        roomId: Long? = null
    ): Result<Attachment>

    suspend fun uploadImage(
        imageBytes: ByteArray,
        fileName: String,
        roomId: Long? = null
    ): Result<Attachment>

    suspend fun uploadVideo(
        videoBytes: ByteArray,
        fileName: String,
        roomId: Long? = null
    ): Result<Attachment>

    suspend fun uploadAudio(
        audioBytes: ByteArray,
        fileName: String,
        roomId: Long? = null
    ): Result<Attachment>

    suspend fun deleteFile(fileId: String): Result<Unit>
}

class FileRepositoryImpl(
    private val fileApiService: FileApiService
) : FileRepository {

    override suspend fun uploadFile(
        fileBytes: ByteArray,
        fileName: String,
        contentType: String,
        roomId: Long?
    ): Result<Attachment> {
        return safeApiCall {
            val response = fileApiService.uploadFile(fileBytes, fileName, contentType, roomId)

            if (response.success && response.data != null) {
                response.data.toAttachment()
            } else {
                throw Exception(response.message ?: "파일 업로드 실패")
            }
        }
    }

    override suspend fun uploadImage(
        imageBytes: ByteArray,
        fileName: String,
        roomId: Long?
    ): Result<Attachment> {
        return safeApiCall {
            val response = fileApiService.uploadImage(imageBytes, fileName, roomId)

            if (response.success && response.data != null) {
                response.data.toAttachment()
            } else {
                throw Exception(response.message ?: "이미지 업로드 실패")
            }
        }
    }

    override suspend fun uploadVideo(
        videoBytes: ByteArray,
        fileName: String,
        roomId: Long?
    ): Result<Attachment> {
        return safeApiCall {
            val response = fileApiService.uploadVideo(videoBytes, fileName, roomId)

            if (response.success && response.data != null) {
                response.data.toAttachment()
            } else {
                throw Exception(response.message ?: "비디오 업로드 실패")
            }
        }
    }

    override suspend fun uploadAudio(
        audioBytes: ByteArray,
        fileName: String,
        roomId: Long?
    ): Result<Attachment> {
        return safeApiCall {
            val response = fileApiService.uploadAudio(audioBytes, fileName, roomId)

            if (response.success && response.data != null) {
                response.data.toAttachment()
            } else {
                throw Exception(response.message ?: "오디오 업로드 실패")
            }
        }
    }

    override suspend fun deleteFile(fileId: String): Result<Unit> {
        return safeApiCall {
            val response = fileApiService.deleteFile(fileId)

            if (!response.success) {
                throw Exception(response.message ?: "파일 삭제 실패")
            }
        }
    }
}
