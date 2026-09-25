package com.krithi.ui.library

import com.krithi.domain.model.Album
import com.krithi.domain.model.Song

sealed interface LibraryUiState {
    object Loading : LibraryUiState
    data class Success(val songs: List<Song>, val albums: List<Album>) : LibraryUiState
    object Empty : LibraryUiState
    object PermissionRequired : LibraryUiState
    data class Error(val message: String) : LibraryUiState
}
