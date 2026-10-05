package com.krithi.data.repository

import com.krithi.data.local.dao.FavoriteDao
import com.krithi.data.local.entity.FavoriteEntity
import com.krithi.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao
) : FavoriteRepository {

    override fun getAllFavoriteSongIds(): Flow<List<Long>> {
        return favoriteDao.getAllFavorites().map { entities ->
            entities.map { it.songId }
        }
    }

    override suspend fun addFavorite(songId: Long) {
        favoriteDao.insertFavorite(FavoriteEntity(songId = songId))
    }

    override suspend fun removeFavorite(songId: Long) {
        favoriteDao.deleteFavoriteBySongId(songId)
    }

    override fun isFavorite(songId: Long): Flow<Boolean> {
        return favoriteDao.isFavorite(songId)
    }
}
