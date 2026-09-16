package com.krithi.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krithi.domain.usecase.GetSongsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getSongsUseCase: GetSongsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LibraryUiState>(LibraryUiState.PermissionRequired)
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

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
                if (songs.isEmpty()) {
                    _uiState.value = LibraryUiState.Empty
                } else {
                    _uiState.value = LibraryUiState.Success(songs)
                }
            } catch (e: Exception) {
                _uiState.value = LibraryUiState.Error(e.message ?: "Failed to load library")
            }
        }
    }
}
