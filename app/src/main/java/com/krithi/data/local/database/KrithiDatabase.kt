package com.krithi.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.krithi.data.local.dao.FavoriteDao
import com.krithi.data.local.dao.PlaylistDao
import com.krithi.data.local.entity.FavoriteEntity
import com.krithi.data.local.entity.PlaylistEntity
import com.krithi.data.local.entity.PlaylistSongEntity

@Database(
    entities = [FavoriteEntity::class, PlaylistEntity::class, PlaylistSongEntity::class],
    version = 1,
    exportSchema = false
)
abstract class KrithiDatabase : RoomDatabase() {
    abstract val favoriteDao: FavoriteDao
    abstract val playlistDao: PlaylistDao
}
