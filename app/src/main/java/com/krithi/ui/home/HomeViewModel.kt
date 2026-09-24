package com.krithi.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krithi.domain.model.Song
import com.krithi.domain.repository.MusicRepository
import com.krithi.playback.PlayerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val playerManager: PlayerManager
) : ViewModel() {

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            playerManager.initialize()
        }
    }

    fun loadSongs() {
        if (_songs.value.isNotEmpty()) return
        viewModelScope.launch {
            _isLoading.value = true
            val loadedSongs = musicRepository.getSongs()
            _songs.value = loadedSongs
            _isLoading.value = false
        }
    }

    fun playSong(song: Song, index: Int) {
        playerManager.playSongs(_songs.value, index)
    }
}
