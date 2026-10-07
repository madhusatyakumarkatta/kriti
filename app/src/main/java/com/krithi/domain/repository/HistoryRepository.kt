package com.krithi.domain.repository

import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getRecentHistorySongIds(): Flow<List<Long>>
    suspend fun addHistory(songId: Long)
}
