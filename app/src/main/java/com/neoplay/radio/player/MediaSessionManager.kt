package com.neoplay.radio.player

import android.content.Context
import androidx.media3.session.MediaSession
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaSessionManager @Inject constructor(
    private val radioPlayer: RadioPlayer
) {
    private var mediaSession: MediaSession? = null

    fun initMediaSession(context: Context): MediaSession? {
        val player = radioPlayer.getPlayer() ?: return null
        if (mediaSession == null) {
            mediaSession = MediaSession.Builder(context, player).build()
        }
        return mediaSession
    }

    fun release() {
        mediaSession?.release()
        mediaSession = null
    }
}
