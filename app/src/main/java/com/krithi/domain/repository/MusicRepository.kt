package com.krithi.domain.repository

import com.krithi.domain.model.Album
import com.krithi.domain.model.Song

interface MusicRepository {
    suspend fun getSongs(): List<Song>
    suspend fun getAlbums(): List<Album>
}
