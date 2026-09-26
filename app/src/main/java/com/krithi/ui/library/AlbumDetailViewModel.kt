package com.krithi.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krithi.domain.model.Album
import com.krithi.domain.usecase.GetAlbumsUseCase
import com.krithi.playback.PlayerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    private val getAlbumsUseCase: GetAlbumsUseCase,
    private val playerManager: PlayerManager
) : ViewModel() {

    private val _album = MutableStateFlow<Album?>(null)
    val album: StateFlow<Album?> = _album.asStateFlow()

    fun loadAlbum(albumId: Long) {
        viewModelScope.launch {
            val albums = getAlbumsUseCase()
            _album.value = albums.find { it.id == albumId }
        }
    }

    fun playSong(index: Int) {
        album.value?.let {
            playerManager.playSongs(it.songs, index)
        }
    }
}
