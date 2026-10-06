package com.krithi.data.repository

import com.krithi.data.local.dao.HistoryDao
import com.krithi.data.local.entity.HistoryEntity
import com.krithi.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val historyDao: HistoryDao
) : HistoryRepository {

    override fun getRecentHistorySongIds(): Flow<List<Long>> {
        return historyDao.getRecentHistory().map { entities ->
            entities.map { it.songId }
        }
    }

    override suspend fun addHistory(songId: Long) {
        historyDao.insertHistory(HistoryEntity(songId = songId))
    }
}
