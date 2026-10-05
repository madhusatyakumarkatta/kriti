package com.krithi.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krithi.domain.model.Album
import com.krithi.domain.model.Playlist
import com.krithi.domain.model.Song
import com.krithi.domain.repository.PlaylistRepository
import com.krithi.domain.usecase.GetAlbumsUseCase
import com.krithi.domain.usecase.GetSongsUseCase
import com.krithi.playback.PlayerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getSongsUseCase: GetSongsUseCase,
    private val getAlbumsUseCase: GetAlbumsUseCase,
    private val playlistRepository: PlaylistRepository,
    private val playerManager: PlayerManager
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _allSongs = MutableStateFlow<List<Song>>(emptyList())
    private val _allAlbums = MutableStateFlow<List<Album>>(emptyList())
    private val _allPlaylists = MutableStateFlow<List<Playlist>>(emptyList())

    val searchResults: StateFlow<SearchResults> = combine(
        _searchQuery,
        _allSongs,
        _allAlbums,
        _allPlaylists
    ) { query, songs, albums, playlists ->
        if (query.isBlank()) {
            SearchResults()
        } else {
            val lowerQuery = query.lowercase()
            SearchResults(
                songs = songs.filter { it.title.lowercase().contains(lowerQuery) || it.artist.lowercase().contains(lowerQuery) },
                albums = albums.filter { it.title.lowercase().contains(lowerQuery) || it.artist.lowercase().contains(lowerQuery) },
                playlists = playlists.filter { it.name.lowercase().contains(lowerQuery) }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SearchResults())

    init {
        viewModelScope.launch {
            _allSongs.value = getSongsUseCase()
            _allAlbums.value = getAlbumsUseCase()
        }
        viewModelScope.launch {
            playlistRepository.getPlaylists().collect {
                _allPlaylists.value = it
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun playSong(song: Song) {
        val currentResults = searchResults.value.songs
        val index = currentResults.indexOf(song)
        if (index != -1) {
            playerManager.playSongs(currentResults, index)
        }
    }
}

data class SearchResults(
    val songs: List<Song> = emptyList(),
    val albums: List<Album> = emptyList(),
    val playlists: List<Playlist> = emptyList()
)
