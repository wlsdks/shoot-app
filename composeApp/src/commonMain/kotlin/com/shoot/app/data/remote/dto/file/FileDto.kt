package com.shoot.app.data.remote.dto.file

import com.shoot.app.domain.model.Attachment
import kotlinx.serialization.Serializable

@Serializable
data class FileUploadResponse(
    val id: String,
    val filename: String,
    val contentType: String,
    val size: Long,
    val url: String,
    val thumbnailUrl: String? = null,
    val uploadedAt: String? = null
)

fun FileUploadResponse.toAttachment(): Attachment {
    return Attachment(
        id = id,
        filename = filename,
        contentType = contentType,
        size = size,
        url = url,
        thumbnailUrl = thumbnailUrl
    )
}

@Serializable
data class FileInfo(
    val id: String,
    val filename: String,
    val contentType: String,
    val size: Long,
    val url: String,
    val thumbnailUrl: String? = null,
    val uploadedBy: Long,
    val roomId: Long? = null,
    val createdAt: String
)
