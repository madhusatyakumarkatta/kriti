package com.krithi.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.content.Intent
import android.media.audiofx.AudioEffect
import android.widget.Toast
import com.krithi.ui.theme.BackgroundDark
import com.krithi.ui.theme.PrimaryTextDark
import com.krithi.ui.theme.SecondaryTextDark
import com.krithi.ui.theme.SurfaceVariantDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        TopAppBar(
            title = { Text("Settings", color = PrimaryTextDark) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PrimaryTextDark)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
        )

        Column(modifier = Modifier.padding(16.dp)) {
            SettingsItem(
                icon = Icons.Default.Equalizer,
                title = "Equalizer",
                subtitle = "Open system equalizer settings",
                onClick = {
                    val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL).apply {
                        putExtra(AudioEffect.EXTRA_AUDIO_SESSION, 0)
                        putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.packageName)
                        putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                    }
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Equalizer not found on this device", Toast.LENGTH_SHORT).show()
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsItem(
                icon = Icons.Default.Info,
                title = "About",
                subtitle = "Krithi Music Player v1.0",
                onClick = { /* TODO */ }
            )
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = PrimaryTextDark,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, color = PrimaryTextDark, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, color = SecondaryTextDark, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
