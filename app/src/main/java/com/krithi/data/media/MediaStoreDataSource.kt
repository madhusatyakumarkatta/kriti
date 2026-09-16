package com.krithi.data.media

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.krithi.domain.model.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MediaStoreDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun getSongs(): List<Song> = withContext(Dispatchers.IO) {
        val songs = mutableListOf<Song>()
        
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.DATA
        )
        
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
        
        context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val artistIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val trackColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            val yearColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                
                // Track number in MediaStore often includes disc number.
                val trackData = cursor.getInt(trackColumn)
                val trackNumber = trackData % 1000
                val discNumber = trackData / 1000
                
                val dataPath = cursor.getString(dataColumn) ?: ""
                val folder = dataPath.substringBeforeLast("/")

                songs.add(
                    Song(
                        id = id,
                        mediaStoreId = id,
                        uri = contentUri,
                        title = cursor.getString(titleColumn) ?: "Unknown",
                        artist = cursor.getString(artistColumn) ?: "Unknown Artist",
                        album = cursor.getString(albumColumn) ?: "Unknown Album",
                        albumId = cursor.getLong(albumIdColumn),
                        artistId = cursor.getLong(artistIdColumn),
                        duration = cursor.getLong(durationColumn),
                        trackNumber = trackNumber,
                        discNumber = discNumber,
                        genre = "", // Genre requires separate query usually, kept blank for now
                        year = cursor.getInt(yearColumn),
                        dateAdded = cursor.getLong(dateAddedColumn),
                        fileSize = cursor.getLong(sizeColumn),
                        mimeType = cursor.getString(mimeTypeColumn) ?: "",
                        folder = folder
                    )
                )
            }
        }
        
        songs
    }
}
