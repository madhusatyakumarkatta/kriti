package com.krithi.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krithi.domain.model.Song
import com.krithi.domain.repository.FavoriteRepository
import com.krithi.domain.usecase.GetSongsUseCase
import com.krithi.playback.PlayerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getSongsUseCase: GetSongsUseCase,
    private val favoriteRepository: FavoriteRepository,
    private val playerManager: PlayerManager
) : ViewModel() {

    private val _favoriteSongs = MutableStateFlow<List<Song>>(emptyList())
    val favoriteSongs: StateFlow<List<Song>> = _favoriteSongs.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _isLoading.value = true
            val allSongs = getSongsUseCase()
            favoriteRepository.getAllFavoriteSongIds().collect { favoriteIds ->
                val favSet = favoriteIds.toSet()
                _favoriteSongs.value = allSongs.filter { favSet.contains(it.id) }
                _isLoading.value = false
            }
        }
    }

    fun playSong(index: Int) {
        playerManager.playSongs(_favoriteSongs.value, index)
    }
}
