package com.krithi.ui.widget.library

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.krithi.data.model.audio.APlayerModel
import com.krithi.ui.theme.LocalTheme

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GlideCover(
  modifier: Modifier = Modifier,
  model: APlayerModel,
  circle: Boolean = true,
  album: Boolean = true
) {
  var coverModifier = modifier
  if (circle) {
    coverModifier = modifier.clip(CircleShape)
  }
  val placeHolder =
    if (album) LocalTheme.current.albumPlaceHolder else LocalTheme.current.artistPlaceHolder
  GlideImage(
    model = model,
    failure = placeholder(placeHolder),
    loading = placeholder(placeHolder),
    contentDescription = null,
    contentScale = ContentScale.Crop,
    modifier = coverModifier
  )
}