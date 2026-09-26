package com.neoplay.radio.player

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.neoplay.radio.data.model.Station
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class PlayerStatus {
    IDLE,
    BUFFERING,
    PLAYING,
    PAUSED,
    ERROR
}

@Singleton
class RadioPlayer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var exoPlayer: ExoPlayer? = null
    private var retryCount = 0
    private val maxRetries = 2
    private var currentStation: Station? = null
    private var currentPlaylist: List<Station> = emptyList()

    private val _status = MutableStateFlow(PlayerStatus.IDLE)
    val status: StateFlow<PlayerStatus> = _status.asStateFlow()

    private val _nowPlayingStation = MutableStateFlow<Station?>(null)
    val nowPlayingStation: StateFlow<Station?> = _nowPlayingStation.asStateFlow()

    private val _sleepTimerMinutes = MutableStateFlow(0)
    val sleepTimerMinutes: StateFlow<Int> = _sleepTimerMinutes.asStateFlow()

    private val timerHandler = Handler(Looper.getMainLooper())
    private val retryHandler = Handler(Looper.getMainLooper())

    private val sleepTimerRunnable = Runnable {
        stop()
        _sleepTimerMinutes.value = 0
    }

    init {
        initPlayer()
    }

    @OptIn(UnstableApi::class)
    private fun initPlayer() {
        synchronized(this) {
            if (exoPlayer != null) return
            try {
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build()

                exoPlayer = ExoPlayer.Builder(context)
                    .setAudioAttributes(audioAttributes, true)
                    .setHandleAudioBecomingNoisy(true)
                    .build()

                exoPlayer?.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_BUFFERING -> _status.value = PlayerStatus.BUFFERING
                            Player.STATE_READY -> {
                                _status.value = if (exoPlayer?.isPlaying == true) PlayerStatus.PLAYING else PlayerStatus.PAUSED
                                retryCount = 0
                            }
                            Player.STATE_ENDED -> _status.value = PlayerStatus.PAUSED
                            Player.STATE_IDLE -> {
                                if (_status.value != PlayerStatus.ERROR) {
                                    _status.value = PlayerStatus.IDLE
                                }
                            }
                        }
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        if (isPlaying) {
                            _status.value = PlayerStatus.PLAYING
                        } else if (_status.value == PlayerStatus.PLAYING) {
                            _status.value = PlayerStatus.PAUSED
                        }
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        _status.value = PlayerStatus.ERROR
                        val failedStation = currentStation
                        if (retryCount < maxRetries && failedStation != null) {
                            retryCount++
                            retryHandler.postDelayed({
                                if (currentStation?.stationuuid == failedStation.stationuuid) {
                                    playStation(failedStation)
                                }
                            }, 2500L * retryCount)
                        }
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
                _status.value = PlayerStatus.ERROR
            }
        }
    }

    fun setPlaylist(stations: List<Station>) {
        if (stations.isNotEmpty()) {
            currentPlaylist = stations
        }
    }

    fun playStation(station: Station, playlist: List<Station> = emptyList()) {
        try {
            retryHandler.removeCallbacksAndMessages(null)
            if (playlist.isNotEmpty()) {
                currentPlaylist = playlist
            }
            currentStation = station
            _nowPlayingStation.value = station

            val streamUrl = station.urlResolved.ifBlank { station.url }.trim()
            if (streamUrl.isBlank()) {
                _status.value = PlayerStatus.ERROR
                return
            }

            val artworkUri = try {
                if (station.favicon.isNotBlank()) Uri.parse(station.favicon.trim()) else null
            } catch (_: Exception) {
                null
            }

            val mediaMetadata = MediaMetadata.Builder()
                .setTitle(station.displayTitle.ifBlank { "Neo Radio" })
                .setArtist(station.country.ifBlank { "Radio" })
                .setArtworkUri(artworkUri)
                .build()

            val mediaItem = MediaItem.Builder()
                .setUri(streamUrl)
                .setMediaMetadata(mediaMetadata)
                .build()

            if (exoPlayer == null) {
                initPlayer()
            }

            exoPlayer?.let { player ->
                player.stop()
                player.clearMediaItems()
                player.setMediaItem(mediaItem)
                player.prepare()
                player.play()
                _status.value = PlayerStatus.BUFFERING
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _status.value = PlayerStatus.ERROR
        }
    }

    fun playNext() {
        try {
            val current = currentStation ?: return
            if (currentPlaylist.isEmpty()) return
            val index = currentPlaylist.indexOfFirst { it.stationuuid == current.stationuuid }
            if (index != -1) {
                val nextIndex = (index + 1) % currentPlaylist.size
                playStation(currentPlaylist[nextIndex])
            } else if (currentPlaylist.isNotEmpty()) {
                playStation(currentPlaylist[0])
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playPrevious() {
        try {
            val current = currentStation ?: return
            if (currentPlaylist.isEmpty()) return
            val index = currentPlaylist.indexOfFirst { it.stationuuid == current.stationuuid }
            if (index != -1) {
                val prevIndex = if (index - 1 < 0) currentPlaylist.size - 1 else index - 1
                playStation(currentPlaylist[prevIndex])
            } else if (currentPlaylist.isNotEmpty()) {
                playStation(currentPlaylist[0])
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun togglePlayPause() {
        try {
            if (exoPlayer == null) initPlayer()
            exoPlayer?.let { player ->
                if (player.isPlaying) {
                    player.pause()
                    _status.value = PlayerStatus.PAUSED
                } else {
                    if (player.playbackState == Player.STATE_IDLE && currentStation != null) {
                        playStation(currentStation!!)
                    } else {
                        player.play()
                        _status.value = PlayerStatus.PLAYING
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {
        try {
            retryHandler.removeCallbacksAndMessages(null)
            exoPlayer?.stop()
            exoPlayer?.clearMediaItems()
            _status.value = PlayerStatus.PAUSED
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setVolume(volume: Float) {
        try {
            exoPlayer?.volume = volume.coerceIn(0f, 1f)
        } catch (_: Exception) {}
    }

    fun setSleepTimer(minutes: Int) {
        try {
            timerHandler.removeCallbacks(sleepTimerRunnable)
            _sleepTimerMinutes.value = minutes
            if (minutes > 0) {
                timerHandler.postDelayed(sleepTimerRunnable, minutes * 60 * 1000L)
            }
        } catch (_: Exception) {}
    }

    fun release() {
        try {
            timerHandler.removeCallbacks(sleepTimerRunnable)
            retryHandler.removeCallbacksAndMessages(null)
            exoPlayer?.release()
            exoPlayer = null
        } catch (_: Exception) {}
    }

    fun getPlayer(): ExoPlayer? = exoPlayer
}
