package com.krithi.ui.library

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Sort
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.krithi.ui.components.AlbumCard
import com.krithi.ui.components.SongRow
import com.krithi.ui.theme.BackgroundDark
import com.krithi.ui.theme.PrimaryAccent
import com.krithi.ui.theme.PrimaryTextDark
import com.krithi.ui.theme.SecondaryTextDark
import android.app.Activity
import androidx.activity.result.IntentSenderRequest
import com.krithi.domain.model.Song
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import com.krithi.ui.theme.SurfaceVariantDark

@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = hiltViewModel(),
    onNavigateToAlbum: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val selectedPlaylistSongs by viewModel.selectedPlaylistSongs.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Songs", "Albums", "Playlists")
    val context = LocalContext.current
    
    var showPlaylistDialog by remember { mutableStateOf<Song?>(null) }
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }
    
    var selectedPlaylistId by remember { mutableStateOf<Long?>(null) }
    
    val deleteLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.loadLibrary()
        }
    }

    val permissionToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) viewModel.onPermissionGranted()
            else viewModel.onPermissionDenied()
        }
    )

    LaunchedEffect(Unit) {
        permissionLauncher.launch(permissionToRequest)
    }

    Column(modifier = modifier.fillMaxSize().background(BackgroundDark)) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = BackgroundDark,
            contentColor = PrimaryTextDark,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = PrimaryAccent
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title, color = if (selectedTabIndex == index) PrimaryAccent else SecondaryTextDark) }
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (val state = uiState) {
                is LibraryUiState.Loading -> CircularProgressIndicator(color = PrimaryAccent)
                is LibraryUiState.PermissionRequired -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Permission required to scan local audio.", color = PrimaryTextDark)
                        Button(
                            onClick = { permissionLauncher.launch(permissionToRequest) },
                            modifier = Modifier.padding(top = 16.dp)
                        ) { Text("Grant Permission") }
                    }
                }
                is LibraryUiState.Empty -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Your library is empty.", color = PrimaryTextDark)
                        Button(
                            onClick = { viewModel.loadLibrary() },
                            modifier = Modifier.padding(top = 16.dp)
                        ) { Text("Scan Library") }
                    }
                }
                is LibraryUiState.Success -> {
                    when (selectedTabIndex) {
                        0 -> { // Songs
                            var showSortMenu by remember { mutableStateOf(false) }
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("All Songs", style = MaterialTheme.typography.titleMedium, color = PrimaryTextDark)
                                    Box {
                                        IconButton(onClick = { showSortMenu = true }) {
                                            Icon(Icons.Default.Sort, contentDescription = "Sort", tint = PrimaryTextDark)
                                        }
                                        DropdownMenu(
                                            expanded = showSortMenu,
                                            onDismissRequest = { showSortMenu = false },
                                            modifier = Modifier.background(SurfaceVariantDark)
                                        ) {
                                            DropdownMenuItem(text = { Text("Title (A-Z)", color = PrimaryTextDark) }, onClick = { viewModel.setSortOption(SortOption.TITLE_ASC); showSortMenu = false })
                                            DropdownMenuItem(text = { Text("Title (Z-A)", color = PrimaryTextDark) }, onClick = { viewModel.setSortOption(SortOption.TITLE_DESC); showSortMenu = false })
                                            DropdownMenuItem(text = { Text("Recently Added", color = PrimaryTextDark) }, onClick = { viewModel.setSortOption(SortOption.DATE_ADDED); showSortMenu = false })
                                            DropdownMenuItem(text = { Text("Duration", color = PrimaryTextDark) }, onClick = { viewModel.setSortOption(SortOption.DURATION_DESC); showSortMenu = false })
                                        }
                                    }
                                }
                                LazyColumn(modifier = Modifier.fillMaxSize()) {
                                    itemsIndexed(state.songs, key = { _, it -> it.id }) { index, song ->
                                        SongRow(song = song, onClick = { 
                                            viewModel.playSongs(state.songs, index)
                                        }, onOptionsClick = { action ->
                                            if (action == "delete") {
                                                val intentSender = viewModel.getDeleteIntentSender(context, song.uri)
                                                if (intentSender != null) {
                                                    deleteLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                                                } else {
                                                    if (viewModel.deleteSongLegacy(context, song.uri)) {
                                                        viewModel.loadLibrary()
                                                    }
                                                }
                                            } else if (action == "add_to_playlist") {
                                                showPlaylistDialog = song
                                            }
                                        })
                                    }
                                }
                            }
                        }
                        1 -> { // Albums
                                LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.albums, key = { it.id }) { album ->
                                    AlbumCard(
                                        album = album,
                                        onClick = { onNavigateToAlbum(album.id) }
                                    )
                                }
                            }
                        }
                        2 -> {
                            if (selectedPlaylistId != null) {
                                val currentPlaylist = playlists.find { it.id == selectedPlaylistId }
                                Column(modifier = Modifier.fillMaxSize()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(onClick = { 
                                            selectedPlaylistId = null
                                            viewModel.selectPlaylist(null) 
                                        }) {
                                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = PrimaryTextDark)
                                        }
                                        Text(currentPlaylist?.name ?: "", style = MaterialTheme.typography.titleLarge, color = PrimaryTextDark, modifier = Modifier.padding(start = 16.dp))
                                    }
                                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                                        itemsIndexed(selectedPlaylistSongs, key = { _, it -> it.id }) { index, song ->
                                            SongRow(song = song, onClick = {
                                                viewModel.playSongs(selectedPlaylistSongs, index)
                                            }, onOptionsClick = { action ->
                                                if (action == "delete") {
                                                    viewModel.removeSongFromPlaylist(selectedPlaylistId!!, song.id)
                                                    viewModel.selectPlaylist(selectedPlaylistId)
                                                }
                                            })
                                        }
                                    }
                                }
                            } else {
                                LazyColumn(modifier = Modifier.fillMaxSize()) {
                                    items(playlists, key = { it.id }) { playlist ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { 
                                                    selectedPlaylistId = playlist.id
                                                    viewModel.selectPlaylist(playlist.id)
                                                }
                                                .padding(16.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(playlist.name, style = MaterialTheme.typography.titleMedium, color = PrimaryTextDark)
                                            IconButton(onClick = { viewModel.deletePlaylist(playlist.id) }) {
                                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Album", tint = SecondaryTextDark)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                is LibraryUiState.Error -> Text("Error: ${state.message}", color = PrimaryTextDark)
            }
        }
    }
    
    if (showPlaylistDialog != null) {
        AlertDialog(
            onDismissRequest = { showPlaylistDialog = null },
            title = { Text("Add to Custom Album", color = PrimaryTextDark) },
            text = {
                Column {
                    if (playlists.isEmpty()) {
                        Text("No custom albums exist yet.", color = SecondaryTextDark)
                    } else {
                        LazyColumn {
                            items(playlists) { playlist ->
                                    TextButton(
                                    onClick = {
                                        viewModel.addSongToPlaylist(playlist.id, showPlaylistDialog!!.id)
                                        showPlaylistDialog = null
                                        Toast.makeText(context, "Added to ${playlist.name}", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    colors = ButtonDefaults.textButtonColors(contentColor = PrimaryTextDark)
                                ) {
                                    Text(playlist.name, style = MaterialTheme.typography.titleMedium)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { showCreatePlaylistDialog = true }) {
                        Text("Create New")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPlaylistDialog = null }) {
                    Text("Close")
                }
            },
            containerColor = BackgroundDark
        )
    }

    if (showCreatePlaylistDialog) {
        AlertDialog(
            onDismissRequest = { showCreatePlaylistDialog = false },
            title = { Text("Create Custom Album", color = PrimaryTextDark) },
            text = {
                OutlinedTextField(
                    value = newPlaylistName,
                    onValueChange = { newPlaylistName = it },
                    label = { Text("Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = PrimaryTextDark,
                        unfocusedTextColor = PrimaryTextDark
                    )
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (newPlaylistName.isNotBlank()) {
                        viewModel.createPlaylist(newPlaylistName)
                        newPlaylistName = ""
                        showCreatePlaylistDialog = false
                    }
                }) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreatePlaylistDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = BackgroundDark
        )
    }
}
