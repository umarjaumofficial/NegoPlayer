/*
 * NegoPlayer - Advanced Android Media Platform
 * Author: Muhammad Umar
 * Project: NegoPlayer
 * File: PlayerViewModel.kt
 */

package com.negoplayer.app.ui.screens.player

import android.app.Activity
import android.app.PictureInPictureParams
import android.content.Context
import android.os.Build
import android.util.Rational
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Represents current player UI state.
 */
data class PlayerUiState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val playbackSpeed: Float = 1.0f,
    val volume: Float = 1.0f,
    val brightness: Float = 0.5f,
    val subtitleTracks: List<SubtitleTrack> = emptyList(),
    val selectedSubtitleTrack: Int = -1,
    val error: String? = null
)

/**
 * ViewModel for the Player screen.
 * Manages Media3 ExoPlayer lifecycle and playback state.
 *
 * Author: Muhammad Umar
 */
@HiltViewModel
class PlayerViewModel @Inject constructor() : ViewModel() {

    private var exoPlayer: ExoPlayer? = null
    private val _playerState = MutableStateFlow(PlayerUiState())
    val playerState: StateFlow<PlayerUiState> = _playerState.asStateFlow()
    private var positionUpdateJob: Job? = null

    /**
     * Initialize ExoPlayer with the given media URI.
     */
    fun initializePlayer(context: Context, mediaUri: String, title: String) {
        releasePlayer()
        exoPlayer = ExoPlayer.Builder(context)
            .build()
            .also { player ->
                val mediaItem = MediaItem.Builder()
                    .setUri(mediaUri)
                    .build()
                player.setMediaItem(mediaItem)
                player.prepare()
                player.playWhenReady = true

                player.addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _playerState.value = _playerState.value.copy(isPlaying = isPlaying)
                        if (isPlaying) startPositionUpdates() else stopPositionUpdates()
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        _playerState.value = _playerState.value.copy(
                            isBuffering = playbackState == Player.STATE_BUFFERING,
                            duration = player.duration.coerceAtLeast(0L)
                        )
                    }
                })
                startPositionUpdates()
            }
    }

    fun getPlayer(): ExoPlayer? = exoPlayer

    fun togglePlayPause() {
        exoPlayer?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    fun seekForward() {
        exoPlayer?.let {
            it.seekTo((it.currentPosition + 10_000L).coerceAtMost(it.duration))
        }
    }

    fun seekBackward() {
        exoPlayer?.let {
            it.seekTo((it.currentPosition - 10_000L).coerceAtLeast(0L))
        }
    }

    fun seekByDelta(deltaMs: Long) {
        exoPlayer?.let {
            it.seekTo((it.currentPosition + deltaMs).coerceIn(0L, it.duration))
        }
    }

    fun seekTo(positionMs: Long) {
        exoPlayer?.seekTo(positionMs)
    }

    fun setPlaybackSpeed(speed: Float) {
        exoPlayer?.setPlaybackSpeed(speed)
        _playerState.value = _playerState.value.copy(playbackSpeed = speed)
    }

    fun adjustVolume(delta: Float) {
        exoPlayer?.let {
            val newVol = (it.volume + delta).coerceIn(0f, 1f)
            it.volume = newVol
            _playerState.value = _playerState.value.copy(volume = newVol)
        }
    }

    fun adjustBrightness(activity: Activity, delta: Float) {
        val layout = activity.window.attributes
        val newBrightness = (layout.screenBrightness + delta).coerceIn(0.01f, 1f)
        layout.screenBrightness = newBrightness
        activity.window.attributes = layout
        _playerState.value = _playerState.value.copy(brightness = newBrightness)
    }

    fun selectSubtitleTrack(index: Int) {
        _playerState.value = _playerState.value.copy(selectedSubtitleTrack = index)
    }

    fun enterPiP(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.enterPictureInPictureMode(
                PictureInPictureParams.Builder()
                    .setAspectRatio(Rational(16, 9))
                    .build()
            )
        }
    }

    private fun startPositionUpdates() {
        positionUpdateJob = viewModelScope.launch {
            while (true) {
                exoPlayer?.let {
                    _playerState.value = _playerState.value.copy(
                        currentPosition = it.currentPosition,
                        duration = it.duration.coerceAtLeast(0L)
                    )
                }
                delay(500)
            }
        }
    }

    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }

    fun releasePlayer() {
        stopPositionUpdates()
        exoPlayer?.release()
        exoPlayer = null
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }
}
