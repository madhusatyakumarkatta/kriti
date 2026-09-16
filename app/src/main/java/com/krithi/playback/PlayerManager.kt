package com.krithi.playback

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.krithi.domain.model.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.guava.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var mediaController: MediaController? = null

    suspend fun initialize() {
        if (mediaController != null) return
        val sessionToken = SessionToken(context, ComponentName(context, MusicService::class.java))
        mediaController = MediaController.Builder(context, sessionToken).buildAsync().await()
    }

    fun playSong(song: Song) {
        val mediaItem = MediaItem.Builder()
            .setMediaId(song.id.toString())
            .setUri(song.uri)
            .build()
        
        mediaController?.apply {
            setMediaItem(mediaItem)
            prepare()
            play()
        }
    }
    
    fun playSongs(songs: List<Song>, startIndex: Int = 0) {
        val mediaItems = songs.map { song ->
            MediaItem.Builder()
                .setMediaId(song.id.toString())
                .setUri(song.uri)
                .build()
        }
        
        mediaController?.apply {
            setMediaItems(mediaItems, startIndex, 0)
            prepare()
            play()
        }
    }

    fun pause() {
        mediaController?.pause()
    }

    fun resume() {
        mediaController?.play()
    }

    fun skipToNext() {
        mediaController?.seekToNext()
    }

    fun skipToPrevious() {
        mediaController?.seekToPrevious()
    }
}
