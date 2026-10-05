package com.krithi.domain.repository

import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getAllFavoriteSongIds(): Flow<List<Long>>
    suspend fun addFavorite(songId: Long)
    suspend fun removeFavorite(songId: Long)
    fun isFavorite(songId: Long): Flow<Boolean>
}
