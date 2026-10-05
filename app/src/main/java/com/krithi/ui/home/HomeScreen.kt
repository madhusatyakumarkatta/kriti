package com.krithi.ui.home

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import com.krithi.ui.theme.SurfaceVariantDark
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.krithi.domain.model.Song
import com.krithi.ui.theme.PrimaryAccent
import com.krithi.ui.theme.PrimaryTextDark
import com.krithi.ui.theme.SecondaryTextDark
import com.krithi.ui.theme.SurfaceVariantDark

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToFavorites: () -> Unit
) {
    val songs by viewModel.songs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var hasPermission by remember { mutableStateOf(false) }

    val permissionToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasPermission = isGranted
            if (isGranted) {
                viewModel.loadSongs()
            }
        }
    )

    LaunchedEffect(Unit) {
        permissionLauncher.launch(permissionToRequest)
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (!hasPermission) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Storage permission is required to find your audio files.",
                    color = PrimaryTextDark,
                    modifier = Modifier.padding(16.dp)
                )
                Button(onClick = { permissionLauncher.launch(permissionToRequest) }) {
                    Text("Grant Permission")
                }
            }
        } else if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (songs.isEmpty()) {
            Text(
                "No songs found on this device.",
                color = PrimaryTextDark,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp) // padding for miniplayer
            ) {
                item {
                    FavoritesBanner(onClick = onNavigateToFavorites)
                }
                itemsIndexed(songs, key = { _, song -> song.id }) { index, song ->
                    SongListItem(
                        song = song,
                        onClick = { viewModel.playSong(song, index) }
                    )
                }
            }
        }
    }
}

@Composable
fun FavoritesBanner(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Favorites",
            tint = PrimaryAccent,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "Your Favorites",
            color = PrimaryTextDark,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun SongListItem(song: Song, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(song.customCoverUri ?: song.uri)
                .crossfade(true)
                .build(),
            contentDescription = "Album Art",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceVariantDark)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                color = PrimaryTextDark,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = song.artist,
                color = SecondaryTextDark,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
