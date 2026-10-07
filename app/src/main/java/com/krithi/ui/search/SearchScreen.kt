package com.krithi.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.krithi.ui.components.SongRow
import com.krithi.ui.theme.BackgroundDark
import com.krithi.ui.theme.PrimaryAccent
import com.krithi.ui.theme.PrimaryTextDark
import com.krithi.ui.theme.SecondaryTextDark
import com.krithi.ui.theme.SurfaceVariantDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
    onNavigateToAlbum: (Long) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search songs, albums, playlists...", color = SecondaryTextDark) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = SecondaryTextDark) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = SecondaryTextDark)
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = PrimaryTextDark,
                unfocusedTextColor = PrimaryTextDark,
                focusedContainerColor = SurfaceVariantDark,
                unfocusedContainerColor = SurfaceVariantDark,
                focusedBorderColor = PrimaryAccent,
                unfocusedBorderColor = SurfaceVariantDark
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (searchQuery.isBlank()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Search for your favorite music", color = SecondaryTextDark)
            }
        } else if (searchResults.songs.isEmpty() && searchResults.albums.isEmpty() && searchResults.playlists.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No results found for \"$searchQuery\"", color = SecondaryTextDark)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                if (searchResults.songs.isNotEmpty()) {
                    item {
                        Text(
                            text = "Songs",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimaryTextDark,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(searchResults.songs) { song ->
                        SongRow(song = song, onClick = { viewModel.playSong(song) })
                    }
                }

                if (searchResults.albums.isNotEmpty()) {
                    item {
                        Text(
                            text = "Albums",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimaryTextDark,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                    }
                    items(searchResults.albums) { album ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable { onNavigateToAlbum(album.id) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(album.title, color = PrimaryTextDark, modifier = Modifier.weight(1f))
                            Text("Album", color = SecondaryTextDark, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                if (searchResults.playlists.isNotEmpty()) {
                    item {
                        Text(
                            text = "Playlists",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimaryTextDark,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                    }
                    items(searchResults.playlists) { playlist ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(playlist.name, color = PrimaryTextDark, modifier = Modifier.weight(1f))
                            Text("Playlist", color = SecondaryTextDark, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}
