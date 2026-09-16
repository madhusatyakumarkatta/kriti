package com.krithi.data.repository

import com.krithi.data.media.MediaStoreDataSource
import com.krithi.domain.model.Song
import com.krithi.domain.repository.MusicRepository
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    private val dataSource: MediaStoreDataSource
) : MusicRepository {
    override suspend fun getSongs(): List<Song> {
        return dataSource.getSongs()
    }
}
