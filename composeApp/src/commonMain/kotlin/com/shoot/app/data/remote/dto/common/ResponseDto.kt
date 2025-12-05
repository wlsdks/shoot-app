package com.shoot.app.data.remote.dto.common

import kotlinx.serialization.Serializable

@Serializable
data class ResponseDto<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val errorCode: String? = null,
    val timestamp: String? = null,
    val code: Int? = null
)
