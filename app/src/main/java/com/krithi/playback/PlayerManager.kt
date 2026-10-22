package com.krithi.playback

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
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
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import com.krithi.domain.repository.HistoryRepository
import com.krithi.widget.KrithiWidgetProvider
import kotlinx.coroutines.sync.Mutex

@Singleton
class PlayerManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val historyRepository: HistoryRepository
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

    private var sleepTimerJob: Job? = null
    private val _sleepTimerTimeRemaining = MutableStateFlow<Long?>(null)
    val sleepTimerTimeRemaining: StateFlow<Long?> = _sleepTimerTimeRemaining.asStateFlow()

    private val initMutex = Mutex()

    suspend fun initialize() {
        initMutex.withLock {
            if (mediaController != null) return@withLock
            val sessionToken = SessionToken(context, ComponentName(context, MusicService::class.java))
            val controller = MediaController.Builder(context, sessionToken).buildAsync().await()

            controller.addListener(object : Player.Listener {
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                    updateCurrentSong(mediaItem)
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    _isPlaying.value = isPlaying
                }
            })
            mediaController = controller
        }
    }

    private fun updateCurrentSong(mediaItem: MediaItem?) {
        val songId = mediaItem?.mediaId?.toLongOrNull()
        _currentSong.value = currentPlaylist.find { it.id == songId }
        
        // Save to history
        songId?.let { id ->
            CoroutineScope(Dispatchers.IO).launch {
                historyRepository.addHistory(id)
            }
        }
        
        // Notify Widget
        val intent = Intent(context, KrithiWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        }
        val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(
            ComponentName(context, KrithiWidgetProvider::class.java)
        )
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        context.sendBroadcast(intent)
    }

    fun playSongs(songs: List<Song>, startIndex: Int = 0) {
        // Limit to 1000 songs to prevent TransactionTooLargeException
        val limitedSongs = if (songs.size > 1000) songs.take(1000) else songs
        val safeStartIndex = if (startIndex >= limitedSongs.size) 0 else startIndex
        
        currentPlaylist = limitedSongs

        val mediaItems = limitedSongs.map { song ->
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
            setMediaItems(mediaItems, safeStartIndex, 0)
            prepare()
            play()
        }
    }

    fun addSongNext(song: Song) {
        val currentIndex = mediaController?.currentMediaItemIndex ?: 0
        val currentSongId = _currentSong.value?.id
        val playlistIndex = currentPlaylist.indexOfFirst { it.id == currentSongId }
        
        val insertPlaylistIndex = if (playlistIndex != -1) playlistIndex + 1 else currentPlaylist.size
        
        val list = currentPlaylist.toMutableList()
        list.add(insertPlaylistIndex, song)
        currentPlaylist = list
        
        val mediaItem = MediaItem.Builder()
            .setMediaId(song.id.toString())
            .setUri(song.uri)
            .setMediaMetadata(MediaMetadata.Builder().setTitle(song.title).setArtist(song.artist).build())
            .build()
            
        mediaController?.addMediaItem(currentIndex + 1, mediaItem)
    }

    fun addSongToQueue(song: Song) {
        val list = currentPlaylist.toMutableList()
        list.add(song)
        currentPlaylist = list
        
        val mediaItem = MediaItem.Builder()
            .setMediaId(song.id.toString())
            .setUri(song.uri)
            .setMediaMetadata(MediaMetadata.Builder().setTitle(song.title).setArtist(song.artist).build())
            .build()
            
        mediaController?.addMediaItem(mediaItem)
    }

    fun moveSongInQueue(fromIndex: Int, toIndex: Int) {
        if (fromIndex == toIndex || fromIndex < 0 || toIndex < 0 || fromIndex >= currentPlaylist.size || toIndex >= currentPlaylist.size) return
        
        val list = currentPlaylist.toMutableList()
        val item = list.removeAt(fromIndex)
        list.add(toIndex, item)
        currentPlaylist = list
        
        mediaController?.moveMediaItem(fromIndex, toIndex)
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
        }
    }

    fun setSleepTimer(minutes: Int) {
        sleepTimerJob?.cancel()
        if (minutes <= 0) {
            _sleepTimerTimeRemaining.value = null
            return
        }
        sleepTimerJob = CoroutineScope(Dispatchers.Main).launch {
            var remaining = minutes * 60L
            while (remaining > 0) {
                _sleepTimerTimeRemaining.value = remaining
                delay(1000)
                remaining--
            }
            _sleepTimerTimeRemaining.value = null
            mediaController?.pause()
        }
    }
}
