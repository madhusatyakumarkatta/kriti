package com.krithi.ui.nowplaying

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.krithi.ui.theme.BackgroundDark
import com.krithi.ui.theme.PrimaryTextDark

@Composable
fun NowPlayingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Now Playing Screen", color = PrimaryTextDark)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Signature Krithi Experience", color = PrimaryTextDark)
        }
    }
}
