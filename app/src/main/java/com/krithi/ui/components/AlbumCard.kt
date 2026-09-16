package com.krithi.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.krithi.domain.model.Album
import com.krithi.ui.theme.PrimaryTextDark
import com.krithi.ui.theme.SecondaryTextDark
import com.krithi.ui.theme.SurfaceVariantDark
import androidx.compose.foundation.background

@Composable
fun AlbumCard(
    album: Album,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(120.dp)
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(album.artworkUri)
                .crossfade(true)
                .build(),
            contentDescription = "Album Art",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(104.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceVariantDark)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = album.title,
            style = MaterialTheme.typography.bodyMedium,
            color = PrimaryTextDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = album.artist,
            style = MaterialTheme.typography.labelSmall,
            color = SecondaryTextDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
