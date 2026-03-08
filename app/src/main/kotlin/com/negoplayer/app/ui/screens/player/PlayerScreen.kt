/*
 * NegoPlayer - Advanced Android Media Platform
 * Author: Muhammad Umar
 * Project: NegoPlayer
 * File: PlayerScreen.kt
 */

package com.negoplayer.app.ui.screens.player

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.View
import android.view.WindowManager
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay
import kotlin.math.abs

/**
 * Full-featured Player Screen with gesture controls, subtitle support,
 * playback speed, PiP mode, and advanced controls.
 *
 * Author: Muhammad Umar
 */
@Composable
fun PlayerScreen(
    mediaUri: String,
    mediaTitle: String,
    onNavigateBack: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as Activity
    val systemUiController = rememberSystemUiController()
    val playerState by viewModel.playerState.collectAsState()
    var showControls by remember { mutableStateOf(true) }
    var showSpeedMenu by remember { mutableStateOf(false) }
    var showSubtitleMenu by remember { mutableStateOf(false) }

    // Initialize player with media
    LaunchedEffect(mediaUri) {
        viewModel.initializePlayer(context, mediaUri, mediaTitle)
    }

    // Auto-hide controls
    LaunchedEffect(showControls) {
        if (showControls && playerState.isPlaying) {
            delay(3500)
            showControls = false
        }
    }

    // Full screen setup
    DisposableEffect(Unit) {
        systemUiController.setSystemBarsColor(Color.Black, darkIcons = false)
        systemUiController.isSystemBarsVisible = false
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            systemUiController.isSystemBarsVisible = true
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            viewModel.releasePlayer()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { showControls = !showControls },
                    onDoubleTap = { offset ->
                        val width = size.width
                        if (offset.x < width / 2) {
                            viewModel.seekBackward()
                        } else {
                            viewModel.seekForward()
                        }
                    }
                )
            }
    ) {
        // ExoPlayer Surface
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    useController = false
                    player = viewModel.getPlayer()
                    setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Gesture overlay
        GestureOverlay(
            onSeek = { viewModel.seekByDelta(it) },
            onVolumeChange = { viewModel.adjustVolume(it) },
            onBrightnessChange = { viewModel.adjustBrightness(activity, it) }
        )

        // Buffering indicator
        if (playerState.isBuffering) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color(0xFF00C853),
                strokeWidth = 4.dp
            )
        }

        // Player Controls Overlay
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(200))
        ) {
            PlayerControlsOverlay(
                title = mediaTitle,
                playerState = playerState,
                onBack = onNavigateBack,
                onPlayPause = { viewModel.togglePlayPause() },
                onSeekForward = { viewModel.seekForward() },
                onSeekBackward = { viewModel.seekBackward() },
                onSeekTo = { viewModel.seekTo(it) },
                onSpeedClick = { showSpeedMenu = true },
                onSubtitleClick = { showSubtitleMenu = true },
                onLockScreen = { showControls = false },
                onPip = { viewModel.enterPiP(activity) }
            )
        }

        // Speed Selection Menu
        if (showSpeedMenu) {
            SpeedSelectionMenu(
                currentSpeed = playerState.playbackSpeed,
                onSpeedSelected = {
                    viewModel.setPlaybackSpeed(it)
                    showSpeedMenu = false
                },
                onDismiss = { showSpeedMenu = false }
            )
        }

        // Subtitle Menu
        if (showSubtitleMenu) {
            SubtitleSelectionMenu(
                tracks = playerState.subtitleTracks,
                selectedTrack = playerState.selectedSubtitleTrack,
                onTrackSelected = {
                    viewModel.selectSubtitleTrack(it)
                    showSubtitleMenu = false
                },
                onDismiss = { showSubtitleMenu = false }
            )
        }
    }
}

@Composable
private fun GestureOverlay(
    onSeek: (Long) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onBrightnessChange: (Float) -> Unit
) {
    var seekPreview by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Left half - brightness
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.5f)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, dragAmount ->
                        onBrightnessChange(-dragAmount / 500f)
                    }
                }
        )

        // Right half - volume
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.5f)
                .align(Alignment.CenterEnd)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, dragAmount ->
                        onVolumeChange(-dragAmount / 500f)
                    }
                }
        )

        // Horizontal seek
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = { seekPreview = null },
                        onHorizontalDrag = { _, dragAmount ->
                            val seekMs = (dragAmount * 150).toLong()
                            onSeek(seekMs)
                            seekPreview = if (seekMs > 0) "+${seekMs / 1000}s" else "${seekMs / 1000}s"
                        }
                    )
                }
        )

        // Seek preview
        seekPreview?.let {
            Surface(
                modifier = Modifier.align(Alignment.Center),
                color = Color.Black.copy(alpha = 0.7f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = it,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PlayerControlsOverlay(
    title: String,
    playerState: PlayerUiState,
    onBack: () -> Unit,
    onPlayPause: () -> Unit,
    onSeekForward: () -> Unit,
    onSeekBackward: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onSpeedClick: () -> Unit,
    onSubtitleClick: () -> Unit,
    onLockScreen: () -> Unit,
    onPip: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.Black.copy(alpha = 0.45f)
            )
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onLockScreen) {
                Icon(Icons.Default.Lock, contentDescription = "Lock", tint = Color.White)
            }
            IconButton(onClick = onPip) {
                Icon(Icons.Default.PictureInPicture, contentDescription = "PiP", tint = Color.White)
            }
            IconButton(onClick = onSubtitleClick) {
                Icon(Icons.Default.Subtitles, contentDescription = "Subtitles", tint = Color.White)
            }
        }

        // Center controls
        Row(
            modifier = Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onSeekBackward,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    Icons.Default.Replay10,
                    contentDescription = "Rewind 10s",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Surface(
                shape = CircleShape,
                color = Color(0xFF00C853),
                modifier = Modifier.size(72.dp)
            ) {
                IconButton(onClick = onPlayPause) {
                    Icon(
                        if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (playerState.isPlaying) "Pause" else "Play",
                        tint = Color.Black,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            IconButton(
                onClick = onSeekForward,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    Icons.Default.Forward10,
                    contentDescription = "Forward 10s",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        // Bottom controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Time display and speed
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${formatTime(playerState.currentPosition)} / ${formatTime(playerState.duration)}",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
                TextButton(onClick = onSpeedClick) {
                    Text(
                        text = "${playerState.playbackSpeed}x",
                        color = Color(0xFF69F0AE),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Seek bar
            Slider(
                value = if (playerState.duration > 0) {
                    playerState.currentPosition.toFloat() / playerState.duration.toFloat()
                } else 0f,
                onValueChange = { fraction ->
                    onSeekTo((fraction * playerState.duration).toLong())
                },
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF00C853),
                    activeTrackColor = Color(0xFF00C853),
                    inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Composable
private fun SpeedSelectionMenu(
    currentSpeed: Float,
    onSpeedSelected: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    val speeds = listOf(0.25f, 0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Playback Speed") },
        text = {
            Column {
                speeds.forEach { speed ->
                    TextButton(
                        onClick = { onSpeedSelected(speed) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "${speed}x",
                            color = if (speed == currentSpeed) Color(0xFF00C853) else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (speed == currentSpeed) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun SubtitleSelectionMenu(
    tracks: List<SubtitleTrack>,
    selectedTrack: Int,
    onTrackSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Subtitle Tracks") },
        text = {
            Column {
                TextButton(
                    onClick = { onTrackSelected(C.INDEX_UNSET) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Off",
                        color = if (selectedTrack == C.INDEX_UNSET) Color(0xFF00C853) else MaterialTheme.colorScheme.onSurface
                    )
                }
                tracks.forEachIndexed { index, track ->
                    TextButton(
                        onClick = { onTrackSelected(index) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            track.label,
                            color = if (selectedTrack == index) Color(0xFF00C853) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        "%d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%02d:%02d".format(minutes, seconds)
    }
}

data class SubtitleTrack(val label: String, val language: String?)
