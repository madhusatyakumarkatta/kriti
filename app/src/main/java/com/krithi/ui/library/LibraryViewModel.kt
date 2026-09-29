package com.krithi.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krithi.domain.usecase.GetAlbumsUseCase
import com.krithi.domain.usecase.GetSongsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import com.krithi.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.krithi.domain.model.Playlist
import com.krithi.domain.model.Song
import kotlinx.coroutines.Job
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.content.IntentSender
import android.os.Build
import com.krithi.playback.PlayerManager

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getSongsUseCase: GetSongsUseCase,
    private val getAlbumsUseCase: GetAlbumsUseCase,
    private val playlistRepository: PlaylistRepository,
    private val playerManager: PlayerManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<LibraryUiState>(LibraryUiState.PermissionRequired)
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()
    
    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()
    
    private val _selectedPlaylistSongs = MutableStateFlow<List<Song>>(emptyList())
    val selectedPlaylistSongs: StateFlow<List<Song>> = _selectedPlaylistSongs.asStateFlow()
    private var playlistSongsJob: Job? = null

    init {
        viewModelScope.launch {
            playerManager.initialize()
        }
        viewModelScope.launch {
            playlistRepository.getPlaylists().collect {
                _playlists.value = it
            }
        }
    }

    fun playSongs(songs: List<Song>, startIndex: Int) {
        playerManager.playSongs(songs, startIndex)
    }

    fun onPermissionGranted() {
        _uiState.value = LibraryUiState.Loading
        loadLibrary()
    }
    
    fun onPermissionDenied() {
        _uiState.value = LibraryUiState.PermissionRequired
    }

    fun loadLibrary() {
        viewModelScope.launch {
            try {
                val songs = getSongsUseCase()
                val albums = getAlbumsUseCase()
                if (songs.isEmpty() && albums.isEmpty()) {
                    _uiState.value = LibraryUiState.Empty
                } else {
                    _uiState.value = LibraryUiState.Success(songs, albums)
                }
            } catch (e: Exception) {
                _uiState.value = LibraryUiState.Error(e.message ?: "Failed to load library")
            }
        }
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            playlistRepository.createPlaylist(name)
        }
    }

    fun addSongToPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            playlistRepository.addSongToPlaylist(playlistId, songId)
        }
    }
    
    fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            playlistRepository.removeSongFromPlaylist(playlistId, songId)
        }
    }

    fun selectPlaylist(playlistId: Long?) {
        playlistSongsJob?.cancel()
        if (playlistId == null) {
            _selectedPlaylistSongs.value = emptyList()
            return
        }
        playlistSongsJob = viewModelScope.launch {
            playlistRepository.getSongsInPlaylist(playlistId).collect { songIds ->
                val allSongs = (_uiState.value as? LibraryUiState.Success)?.songs ?: emptyList()
                val songMap = allSongs.associateBy { it.id }
                _selectedPlaylistSongs.value = songIds.mapNotNull { songMap[it] }
            }
        }
    }
    
    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            playlistRepository.deletePlaylist(playlistId)
        }
    }

    fun getDeleteIntentSender(context: Context, uri: Uri): IntentSender? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            MediaStore.createDeleteRequest(context.contentResolver, listOf(uri)).intentSender
        } else {
            null
        }
    }

    fun deleteSongLegacy(context: Context, uri: Uri): Boolean {
        return try {
            context.contentResolver.delete(uri, null, null) > 0
        } catch (e: Exception) {
            false
        }
    }
}
