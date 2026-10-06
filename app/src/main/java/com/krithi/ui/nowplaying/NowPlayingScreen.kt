package com.krithi.ui.nowplaying

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.Player
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.imageLoader
import androidx.palette.graphics.Palette
import androidx.core.graphics.drawable.toBitmap
import com.krithi.ui.SharedPlaybackViewModel
import com.krithi.ui.theme.BackgroundDark
import com.krithi.ui.theme.PrimaryAccent
import com.krithi.ui.theme.PrimaryTextDark
import com.krithi.ui.theme.SecondaryTextDark
import com.krithi.ui.theme.SurfaceVariantDark
import java.util.Locale
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    viewModel: SharedPlaybackViewModel = hiltViewModel()
) {
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val customCoverUri by viewModel.customCoverUri.collectAsState()
    val repeatMode by viewModel.repeatMode.collectAsState()
    val shuffleModeEnabled by viewModel.shuffleModeEnabled.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val currentPlaylist by viewModel.currentPlaylist.collectAsState()
    val sleepTimerTimeRemaining by viewModel.sleepTimerTimeRemaining.collectAsState()

    val context = LocalContext.current
    
    val showLyrics = remember { mutableStateOf(false) }
    
    val dominantColor = remember { mutableStateOf(BackgroundDark) }
    val animatedBackgroundColor by animateColorAsState(
        targetValue = dominantColor.value,
        animationSpec = tween(durationMillis = 1000)
    )

    val currentUri = customCoverUri ?: currentSong?.uri?.toString()
    LaunchedEffect(currentUri) {
        if (currentUri != null) {
            try {
                val request = ImageRequest.Builder(context)
                    .data(currentUri)
                    .allowHardware(false)
                    .build()
                val result = context.imageLoader.execute(request)
                if (result is SuccessResult) {
                    val bitmap = result.drawable.toBitmap()
                    Palette.from(bitmap).generate { palette ->
                        palette?.dominantSwatch?.rgb?.let { colorInt ->
                            val color = Color(colorInt)
                            dominantColor.value = Color(
                                red = color.red,
                                green = color.green,
                                blue = color.blue,
                                alpha = 1f
                            ).copy(alpha = 0.5f) // Mix it with dark background by turning down alpha
                        } ?: run {
                            dominantColor.value = BackgroundDark
                        }
                    }
                }
            } catch (e: Exception) {
                dominantColor.value = BackgroundDark
            }
        } else {
            dominantColor.value = BackgroundDark
        }
    }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, flag)
                currentSong?.let { song ->
                    viewModel.setCustomCover(song.id, uri.toString())
                }
            }
        }
    )

    val showQueueBottomSheet = remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(animatedBackgroundColor)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Back", tint = PrimaryTextDark)
            }
            Row {
                Text("Now Playing", style = MaterialTheme.typography.titleMedium, color = if (!showLyrics.value) PrimaryTextDark else SecondaryTextDark, modifier = Modifier.clickable { showLyrics.value = false }.padding(horizontal = 8.dp))
                Text("Lyrics", style = MaterialTheme.typography.titleMedium, color = if (showLyrics.value) PrimaryTextDark else SecondaryTextDark, modifier = Modifier.clickable { showLyrics.value = true }.padding(horizontal = 8.dp))
            }
            Row {
                val showSleepTimerMenu = remember { mutableStateOf(false) }
                Box {
                    IconButton(onClick = { showSleepTimerMenu.value = true }) {
                        Icon(Icons.Default.Timer, contentDescription = "Sleep Timer", tint = if (sleepTimerTimeRemaining != null) PrimaryAccent else PrimaryTextDark)
                    }
                    DropdownMenu(
                        expanded = showSleepTimerMenu.value,
                        onDismissRequest = { showSleepTimerMenu.value = false },
                        modifier = Modifier.background(SurfaceVariantDark)
                    ) {
                        listOf(0, 5, 15, 30, 45, 60).forEach { mins ->
                            DropdownMenuItem(
                                text = { Text(if (mins == 0) "Off" else "$mins minutes", color = PrimaryTextDark) },
                                onClick = {
                                    viewModel.setSleepTimer(mins)
                                    showSleepTimerMenu.value = false
                                }
                            )
                        }
                    }
                }
                IconButton(onClick = { showQueueBottomSheet.value = true }) {
                    Icon(Icons.Default.Menu, contentDescription = "Queue", tint = PrimaryTextDark)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (showLyrics.value) {
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(SurfaceVariantDark.copy(alpha = 0.5f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Lyrics not found for this song.\n\n(ID3 Lyrics extraction coming soon)",
                    color = SecondaryTextDark,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Album Art
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(SurfaceVariantDark)
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(customCoverUri ?: currentSong?.uri ?: "https://placeholder.com/500") 
                        .crossfade(true)
                        .build(),
                    contentDescription = "Large Album Art",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                
                val showRenameDialog = remember { mutableStateOf(false) }
                
                // Edit icon overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .background(BackgroundDark.copy(alpha = 0.7f), RoundedCornerShape(50))
                        .clickable { showRenameDialog.value = true }
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Song Name",
                        tint = PrimaryTextDark,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                if (showRenameDialog.value) {
                    val newName = remember { mutableStateOf(currentSong?.title ?: "") }
                    AlertDialog(
                        onDismissRequest = { showRenameDialog.value = false },
                        title = { Text("Rename Song", color = PrimaryTextDark) },
                        text = {
                            OutlinedTextField(
                                value = newName.value,
                                onValueChange = { newName.value = it },
                                label = { Text("Song Name") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = PrimaryTextDark,
                                    unfocusedTextColor = PrimaryTextDark
                                )
                            )
                        },
                        confirmButton = {
                            Button(onClick = {
                                currentSong?.let {
                                    viewModel.renameSong(it.id, newName.value)
                                }
                                showRenameDialog.value = false
                            }) {
                                Text("Save")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showRenameDialog.value = false }) {
                                Text("Cancel")
                            }
                        },
                        containerColor = BackgroundDark
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Title and Artist
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = currentSong?.title ?: "Unknown Song",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = PrimaryTextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = currentSong?.artist ?: "Unknown Artist",
                    style = MaterialTheme.typography.bodyLarge,
                    color = SecondaryTextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = { viewModel.toggleFavorite() }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, 
                    contentDescription = "Favorite", 
                    tint = if (isFavorite) PrimaryAccent else PrimaryTextDark
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Progress Bar
        val progress = if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f
        Slider(
            value = progress,
            onValueChange = { newProgress ->
                viewModel.seekTo((newProgress * duration).toLong())
            },
            colors = SliderDefaults.colors(
                thumbColor = PrimaryTextDark,
                activeTrackColor = PrimaryAccent,
                inactiveTrackColor = SurfaceVariantDark
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(formatDuration(currentPosition), style = MaterialTheme.typography.labelSmall, color = SecondaryTextDark)
            Text(formatDuration(duration), style = MaterialTheme.typography.labelSmall, color = SecondaryTextDark)
        }

        Spacer(modifier = Modifier.weight(1f))

        // Playback Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.toggleShuffleMode() }) { 
                Icon(
                    imageVector = Icons.Default.Shuffle, 
                    contentDescription = "Shuffle", 
                    tint = if (shuffleModeEnabled) PrimaryAccent else PrimaryTextDark
                ) 
            }
            IconButton(onClick = { viewModel.skipToPrevious() }) { Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", tint = PrimaryTextDark, modifier = Modifier.size(36.dp)) }
            
            FloatingActionButton(
                onClick = { viewModel.togglePlayPause() },
                containerColor = PrimaryAccent,
                contentColor = BackgroundDark,
                shape = RoundedCornerShape(50)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, 
                    contentDescription = if (isPlaying) "Pause" else "Play", 
                    modifier = Modifier.size(36.dp)
                )
            }
            
            IconButton(onClick = { viewModel.skipToNext() }) { Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = PrimaryTextDark, modifier = Modifier.size(36.dp)) }
            IconButton(onClick = { viewModel.toggleRepeatMode() }) { 
                Icon(
                    imageVector = if (repeatMode == Player.REPEAT_MODE_ONE) Icons.Default.RepeatOne else Icons.Default.Repeat,
                    contentDescription = "Repeat", 
                    tint = if (repeatMode == Player.REPEAT_MODE_ONE) PrimaryAccent else PrimaryTextDark
                ) 
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
    
    if (showQueueBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = { showQueueBottomSheet.value = false },
            containerColor = BackgroundDark
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(
                    text = "Up Next",
                    style = MaterialTheme.typography.titleLarge,
                    color = PrimaryTextDark,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f, fill = false)
                ) {
                    items(currentPlaylist.size, key = { currentPlaylist[it].id }) { index ->
                        val song = currentPlaylist[index]
                        val isCurrent = song.id == currentSong?.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isCurrent) SurfaceVariantDark else BackgroundDark, RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.playSongs(currentPlaylist, index)
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = song.title,
                                    color = if (isCurrent) PrimaryAccent else PrimaryTextDark,
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
                            if (isCurrent) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Playing",
                                    tint = PrimaryAccent
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%d:%02d", minutes, seconds)
}
