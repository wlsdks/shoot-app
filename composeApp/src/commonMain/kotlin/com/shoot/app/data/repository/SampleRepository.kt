package com.shoot.app.data.repository

import kotlinx.coroutines.flow.Flow

interface SampleRepository {
    suspend fun getSampleData(): Result<String>
    fun observeSampleData(): Flow<List<String>>
}
