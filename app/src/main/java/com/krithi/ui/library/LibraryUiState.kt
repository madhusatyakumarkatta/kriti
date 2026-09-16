package com.krithi.ui.library

import com.krithi.domain.model.Song

sealed interface LibraryUiState {
    object Loading : LibraryUiState
    data class Success(val songs: List<Song>) : LibraryUiState
    object Empty : LibraryUiState
    object PermissionRequired : LibraryUiState
    data class Error(val message: String) : LibraryUiState
}
