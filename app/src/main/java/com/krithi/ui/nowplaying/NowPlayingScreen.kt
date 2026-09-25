package com.krithi.ui.nowplaying

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.krithi.ui.SharedPlaybackViewModel
import com.krithi.ui.theme.BackgroundDark
import com.krithi.ui.theme.PrimaryAccent
import com.krithi.ui.theme.PrimaryTextDark
import com.krithi.ui.theme.SecondaryTextDark
import com.krithi.ui.theme.SurfaceVariantDark
import java.util.Locale

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

    val context = LocalContext.current
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
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
            Text("Now Playing", style = MaterialTheme.typography.titleMedium, color = PrimaryTextDark)
            IconButton(onClick = { /* TODO: Info */ }) {
                Icon(Icons.Default.Info, contentDescription = "Info", tint = PrimaryTextDark)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

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
            
            // Edit icon overlay
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .background(BackgroundDark.copy(alpha = 0.7f), RoundedCornerShape(50))
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Cover",
                    tint = PrimaryTextDark,
                    modifier = Modifier.size(24.dp)
                )
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
            IconButton(onClick = { /* TODO: Favorite */ }) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorite", tint = PrimaryTextDark)
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
            IconButton(onClick = {}) { Icon(Icons.Default.Shuffle, contentDescription = "Shuffle", tint = PrimaryTextDark) }
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
            IconButton(onClick = {}) { Icon(Icons.Default.Repeat, contentDescription = "Repeat", tint = PrimaryTextDark) }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

private fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%d:%02d", minutes, seconds)
}
