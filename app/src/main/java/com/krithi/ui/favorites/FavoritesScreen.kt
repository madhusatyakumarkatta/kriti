package com.krithi.ui.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.krithi.ui.components.SongRow
import com.krithi.ui.theme.BackgroundDark
import com.krithi.ui.theme.PrimaryTextDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val favoriteSongs by viewModel.favoriteSongs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(modifier = modifier.fillMaxSize().background(BackgroundDark)) {
        TopAppBar(
            title = { Text("Favorites", color = PrimaryTextDark) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PrimaryTextDark)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (favoriteSongs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No favorite songs yet.", color = PrimaryTextDark)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                itemsIndexed(favoriteSongs, key = { _, song -> song.id }) { index, song ->
                    SongRow(
                        song = song,
                        onClick = { viewModel.playSong(index) }
                    )
                }
            }
        }
    }
}
