package com.krithi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "playlist_songs", primaryKeys = ["playlistId", "songId"])
data class PlaylistSongEntity(
    val playlistId: Long,
    val songId: Long,
    val position: Int
)
