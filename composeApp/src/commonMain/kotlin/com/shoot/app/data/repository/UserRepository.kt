package com.shoot.app.data.repository

import com.shoot.app.data.remote.api.UserApiService
import com.shoot.app.data.remote.dto.user.ChangePasswordRequest
import com.shoot.app.data.remote.dto.user.UpdateProfileRequest
import com.shoot.app.data.remote.dto.user.toDomain
import com.shoot.app.domain.model.User
import com.shoot.app.utils.safeApiCall

interface UserRepository {
    suspend fun updateProfile(
        nickname: String? = null,
        bio: String? = null,
        profileImageUrl: String? = null
    ): Result<User>

    suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit>

    suspend fun deleteAccount(): Result<Unit>

    suspend fun getUserById(userId: Long): Result<User>

    suspend fun getUserByUsername(username: String): Result<User>
}

class UserRepositoryImpl(
    private val userApiService: UserApiService
) : UserRepository {

    override suspend fun updateProfile(
        nickname: String?,
        bio: String?,
        profileImageUrl: String?
    ): Result<User> {
        return safeApiCall {
            val response = userApiService.updateProfile(
                UpdateProfileRequest(
                    nickname = nickname,
                    bio = bio,
                    profileImageUrl = profileImageUrl
                )
            )

            if (response.success && response.data != null) {
                response.data.toDomain()
            } else {
                throw Exception(response.message ?: "프로필 업데이트 실패")
            }
        }
    }

    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit> {
        return safeApiCall {
            val response = userApiService.changePassword(
                ChangePasswordRequest(
                    currentPassword = currentPassword,
                    newPassword = newPassword
                )
            )

            if (!response.success) {
                throw Exception(response.message ?: "비밀번호 변경 실패")
            }
        }
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return safeApiCall {
            val response = userApiService.deleteAccount()

            if (!response.success) {
                throw Exception(response.message ?: "회원 탈퇴 실패")
            }
        }
    }

    override suspend fun getUserById(userId: Long): Result<User> {
        return safeApiCall {
            val response = userApiService.getUserById(userId)

            if (response.success && response.data != null) {
                response.data.toDomain()
            } else {
                throw Exception(response.message ?: "사용자 정보 조회 실패")
            }
        }
    }

    override suspend fun getUserByUsername(username: String): Result<User> {
        return safeApiCall {
            val response = userApiService.getUserByUsername(username)

            if (response.success && response.data != null) {
                response.data.toDomain()
            } else {
                throw Exception(response.message ?: "사용자 정보 조회 실패")
            }
        }
    }
}
