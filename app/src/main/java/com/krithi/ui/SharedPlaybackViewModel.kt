package com.krithi.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.krithi.domain.model.Song
import com.krithi.playback.PlayerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.krithi.domain.repository.CoverRepository
import com.krithi.domain.repository.FavoriteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import com.krithi.domain.usecase.ExtractLyricsUseCase

@HiltViewModel
class SharedPlaybackViewModel @Inject constructor(
    private val playerManager: PlayerManager,
    private val coverRepository: CoverRepository,
    private val favoriteRepository: FavoriteRepository,
    private val extractLyricsUseCase: ExtractLyricsUseCase
) : ViewModel() {

    val currentPlaylist: StateFlow<List<Song>> = playerManager.currentPlaylistFlow
    val currentSong: StateFlow<Song?> = playerManager.currentSong
    val sleepTimerTimeRemaining: StateFlow<Long?> = playerManager.sleepTimerTimeRemaining
    val isPlaying: StateFlow<Boolean> = playerManager.isPlaying

    @OptIn(ExperimentalCoroutinesApi::class)
    val customCoverUri: StateFlow<String?> = currentSong.flatMapLatest { song ->
        if (song != null) {
            coverRepository.observeCustomCoverUri(song.id)
        } else {
            flowOf(null)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    
    private val _lyrics = MutableStateFlow<String?>(null)
    val lyrics: StateFlow<String?> = _lyrics.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val isFavorite: StateFlow<Boolean> = currentSong.flatMapLatest { song ->
        if (song != null) {
            favoriteRepository.isFavorite(song.id)
        } else {
            flowOf(false)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()
    
    private val _repeatMode = MutableStateFlow(Player.REPEAT_MODE_OFF)
    val repeatMode: StateFlow<Int> = _repeatMode.asStateFlow()

    private val _shuffleModeEnabled = MutableStateFlow(false)
    val shuffleModeEnabled: StateFlow<Boolean> = _shuffleModeEnabled.asStateFlow()

    init {
        viewModelScope.launch {
            while (isActive) {
                if (isPlaying.value) {
                    _currentPosition.value = playerManager.getCurrentPosition()
                    _duration.value = playerManager.getDuration()
                    _repeatMode.value = playerManager.getRepeatMode()
                    _shuffleModeEnabled.value = playerManager.getShuffleModeEnabled()
                }
                delay(1000) // Update every second
            }
        }
        
        viewModelScope.launch {
            currentSong.collect { song ->
                if (song != null) {
                    _lyrics.value = extractLyricsUseCase(song.uri)
                } else {
                    _lyrics.value = null
                }
            }
        }
    }

    fun playSongs(songs: List<Song>, startIndex: Int = 0) {
        playerManager.playSongs(songs, startIndex)
    }

    fun togglePlayPause() {
        playerManager.togglePlayPause()
    }

    fun skipToNext() {
        playerManager.skipToNext()
    }

    fun skipToPrevious() {
        playerManager.skipToPrevious()
    }

    fun seekTo(position: Long) {
        playerManager.seekTo(position)
        _currentPosition.value = position
    }
    
    fun toggleRepeatMode() {
        playerManager.toggleRepeatMode()
        _repeatMode.value = playerManager.getRepeatMode()
    }
    
    fun toggleShuffleMode() {
        playerManager.toggleShuffleMode()
        _shuffleModeEnabled.value = playerManager.getShuffleModeEnabled()
    }

    fun setCustomCover(songId: Long, uri: String?) {
        coverRepository.setCustomCoverUri(songId, uri)
    }

    fun renameSong(songId: Long, newName: String) {
        coverRepository.setCustomTitle(songId, newName)
        playerManager.renameSong(songId, newName)
    }

    fun toggleFavorite() {
        val song = currentSong.value ?: return
        viewModelScope.launch {
            if (isFavorite.value) {
                favoriteRepository.removeFavorite(song.id)
            } else {
                favoriteRepository.addFavorite(song.id)
            }
        }
    }

    fun setSleepTimer(minutes: Int) {
        playerManager.setSleepTimer(minutes)
    }
}
