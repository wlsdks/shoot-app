package com.shoot.app.data.remote.api

import com.shoot.app.data.remote.dto.common.ResponseDto
import com.shoot.app.data.remote.dto.file.FileUploadResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class FileApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String = "http://localhost:8100/api/v1"
) {

    suspend fun uploadFile(
        fileBytes: ByteArray,
        fileName: String,
        contentType: String,
        roomId: Long? = null
    ): ResponseDto<FileUploadResponse> {
        return httpClient.submitFormWithBinaryData(
            url = "$baseUrl/files/upload",
            formData = formData {
                append("file", fileBytes, Headers.build {
                    append(HttpHeaders.ContentType, contentType)
                    append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                })
                roomId?.let { append("roomId", it.toString()) }
            }
        ).body()
    }

    suspend fun uploadImage(
        imageBytes: ByteArray,
        fileName: String,
        roomId: Long? = null
    ): ResponseDto<FileUploadResponse> {
        val contentType = when {
            fileName.endsWith(".png", ignoreCase = true) -> "image/png"
            fileName.endsWith(".gif", ignoreCase = true) -> "image/gif"
            fileName.endsWith(".webp", ignoreCase = true) -> "image/webp"
            else -> "image/jpeg"
        }
        return uploadFile(imageBytes, fileName, contentType, roomId)
    }

    suspend fun uploadVideo(
        videoBytes: ByteArray,
        fileName: String,
        roomId: Long? = null
    ): ResponseDto<FileUploadResponse> {
        val contentType = when {
            fileName.endsWith(".mp4", ignoreCase = true) -> "video/mp4"
            fileName.endsWith(".webm", ignoreCase = true) -> "video/webm"
            fileName.endsWith(".mov", ignoreCase = true) -> "video/quicktime"
            else -> "video/mp4"
        }
        return uploadFile(videoBytes, fileName, contentType, roomId)
    }

    suspend fun uploadAudio(
        audioBytes: ByteArray,
        fileName: String,
        roomId: Long? = null
    ): ResponseDto<FileUploadResponse> {
        val contentType = when {
            fileName.endsWith(".mp3", ignoreCase = true) -> "audio/mpeg"
            fileName.endsWith(".wav", ignoreCase = true) -> "audio/wav"
            fileName.endsWith(".ogg", ignoreCase = true) -> "audio/ogg"
            fileName.endsWith(".m4a", ignoreCase = true) -> "audio/mp4"
            else -> "audio/mpeg"
        }
        return uploadFile(audioBytes, fileName, contentType, roomId)
    }

    suspend fun getFileInfo(fileId: String): ResponseDto<FileUploadResponse> {
        return httpClient.get("$baseUrl/files/$fileId").body()
    }

    suspend fun deleteFile(fileId: String): ResponseDto<Unit> {
        return httpClient.delete("$baseUrl/files/$fileId").body()
    }
}
