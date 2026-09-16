package com.krithi.domain.model

import android.net.Uri

data class Song(
    val id: Long,
    val mediaStoreId: Long,
    val uri: Uri,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val artistId: Long,
    val duration: Long,
    val trackNumber: Int,
    val discNumber: Int,
    val genre: String,
    val year: Int,
    val dateAdded: Long,
    val fileSize: Long,
    val mimeType: String,
    val folder: String
)
