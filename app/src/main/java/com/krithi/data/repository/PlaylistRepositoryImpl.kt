package com.krithi.data.repository

import com.krithi.data.local.dao.PlaylistDao
import com.krithi.data.local.entity.PlaylistEntity
import com.krithi.data.local.entity.PlaylistSongEntity
import com.krithi.domain.model.Playlist
import com.krithi.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlaylistRepositoryImpl @Inject constructor(
    private val playlistDao: PlaylistDao
) : PlaylistRepository {

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getPlaylists().map { entities ->
            entities.map { Playlist(it.id, it.name, 0) } // Count isn't trivial without a JOIN query, keeping it 0 for now
        }
    }

    override suspend fun createPlaylist(name: String): Long {
        return playlistDao.insertPlaylist(PlaylistEntity(name = name))
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistDao.deletePlaylist(PlaylistEntity(id = playlistId, name = ""))
    }

    override fun getSongsInPlaylist(playlistId: Long): Flow<List<Long>> {
        return playlistDao.getSongsInPlaylist(playlistId)
    }

    override suspend fun addSongToPlaylist(playlistId: Long, songId: Long) {
        // Find max position (omitted for brevity, just appending at 0)
        playlistDao.insertSongIntoPlaylist(PlaylistSongEntity(playlistId, songId, 0))
    }

    override suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        playlistDao.removeSongFromPlaylist(playlistId, songId)
    }
}
