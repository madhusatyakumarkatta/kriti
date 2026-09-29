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
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.content.IntentSender
import android.os.Build

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getSongsUseCase: GetSongsUseCase,
    private val getAlbumsUseCase: GetAlbumsUseCase,
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LibraryUiState>(LibraryUiState.PermissionRequired)
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()
    
    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    init {
        viewModelScope.launch {
            playlistRepository.getPlaylists().collect {
                _playlists.value = it
            }
        }
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
