package com.krithi.playback

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.krithi.domain.model.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.guava.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var mediaController: MediaController? = null

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPlaylistFlow = MutableStateFlow<List<Song>>(emptyList())
    val currentPlaylistFlow: StateFlow<List<Song>> = _currentPlaylistFlow.asStateFlow()

    private var currentPlaylist: List<Song>
        get() = _currentPlaylistFlow.value
        set(value) { _currentPlaylistFlow.value = value }

    suspend fun initialize() {
        if (mediaController != null) return
        val sessionToken = SessionToken(context, ComponentName(context, MusicService::class.java))
        mediaController = MediaController.Builder(context, sessionToken).buildAsync().await()

        mediaController?.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                updateCurrentSong(mediaItem)
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                _isPlaying.value = isPlaying
            }
        })
    }

    private fun updateCurrentSong(mediaItem: MediaItem?) {
        val songId = mediaItem?.mediaId?.toLongOrNull()
        _currentSong.value = currentPlaylist.find { it.id == songId }
    }

    fun playSongs(songs: List<Song>, startIndex: Int = 0) {
        currentPlaylist = songs
        val mediaItems = songs.map { song ->
            MediaItem.Builder()
                .setMediaId(song.id.toString())
                .setUri(song.uri)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(song.title)
                        .setArtist(song.artist)
                        .build()
                )
                .build()
        }
        
        mediaController?.apply {
            setMediaItems(mediaItems, startIndex, 0)
            prepare()
            play()
        }
    }

    fun togglePlayPause() {
        mediaController?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.play()
            }
        }
    }

    fun skipToNext() {
        mediaController?.seekToNext()
    }

    fun skipToPrevious() {
        mediaController?.seekToPrevious()
    }

    fun getCurrentPosition(): Long = mediaController?.currentPosition ?: 0L
    fun getDuration(): Long = mediaController?.duration?.takeIf { it > 0 } ?: 0L
    fun seekTo(position: Long) {
        mediaController?.seekTo(position)
    }
    
    fun toggleRepeatMode() {
        mediaController?.let { controller ->
            val currentMode = controller.repeatMode
            controller.repeatMode = if (currentMode == Player.REPEAT_MODE_ONE) {
                Player.REPEAT_MODE_OFF
            } else {
                Player.REPEAT_MODE_ONE
            }
        }
    }
    
    fun getRepeatMode(): Int = mediaController?.repeatMode ?: Player.REPEAT_MODE_OFF

    fun toggleShuffleMode() {
        mediaController?.let { controller ->
            controller.shuffleModeEnabled = !controller.shuffleModeEnabled
        }
    }
    
    fun getShuffleModeEnabled(): Boolean = mediaController?.shuffleModeEnabled ?: false

    fun renameSong(songId: Long, newName: String) {
        // Find in current playlist
        val index = currentPlaylist.indexOfFirst { it.id == songId }
        if (index != -1) {
            val updatedSong = currentPlaylist[index].copy(title = newName)
            val updatedList = currentPlaylist.toMutableList()
            updatedList[index] = updatedSong
            currentPlaylist = updatedList
            
            // If it's currently playing, update the StateFlow
            if (_currentSong.value?.id == songId) {
                _currentSong.value = updatedSong
            }
            
            // Note: Ideally, this should update the underlying Database or MediaStore
            // but for instant UI reflection during playback, we update the runtime models here.
        }
    }
}
