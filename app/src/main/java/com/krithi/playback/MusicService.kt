package com.krithi.playback

import android.content.Intent
import androidx.media3.common.Player
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : MediaLibraryService() {

    @Inject
    lateinit var player: Player

    private var mediaLibrarySession: MediaLibrarySession? = null

    override fun onCreate() {
        super.onCreate()
        
        val callback = object : MediaLibrarySession.Callback {}

        mediaLibrarySession = MediaLibrarySession.Builder(this, player, callback)
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaLibrarySession
    }

    override fun onDestroy() {
        mediaLibrarySession?.run {
            player.release()
            release()
            mediaLibrarySession = null
        }
        super.onDestroy()
    }
}
