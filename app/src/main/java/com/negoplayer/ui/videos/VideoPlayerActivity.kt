package com.negoplayer.ui.videos

import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.negoplayer.ui.common.playerGestures
import com.negoplayer.ui.theme.NegoPlayerTheme
import com.negoplayer.utils.Constants
import com.negoplayer.utils.toReadableDuration
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Full-screen video player activity using Media3 ExoPlayer.
 * Supports gesture controls for brightness, volume, and seeking.
 */
@UnstableApi
class VideoPlayerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Keep screen on during playback
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val uriString = intent.getStringExtra(Constants.EXTRA_MEDIA_URI) ?: run {
            finish()
            return
        }
        val title = intent.getStringExtra(Constants.EXTRA_MEDIA_TITLE) ?: ""

        setContent {
            NegoPlayerTheme(darkTheme = true) {
                VideoPlayerScreen(
                    uri = Uri.parse(uriString),
                    title = title,
                    onBack = { finish() }
                )
            }
        }
    }

    override fun onDestroy() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onDestroy()
    }
}

@UnstableApi
@Composable
fun VideoPlayerScreen(
    uri: Uri,
    title: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }

    var showControls by remember { mutableStateOf(true) }
    var isPlaying by remember { mutableStateOf(true) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var isBuffering by remember { mutableStateOf(true) }
    var gestureIndicator by remember { mutableStateOf("") }

    // Current system volume
    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    var currentVolume by remember {
        mutableFloatStateOf(
            audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() / maxVolume
        )
    }
    var currentBrightness by remember {
        mutableFloatStateOf(
            try {
                Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS) / 255f
            } catch (_: Exception) { 0.5f }
        )
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            playWhenReady = true
        }
    }

    // Listen to player state
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) { isPlaying = playing }
            override fun onPlaybackStateChanged(state: Int) {
                isBuffering = state == Player.STATE_BUFFERING
                if (state == Player.STATE_READY) {
                    duration = exoPlayer.duration.coerceAtLeast(0L)
                }
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Poll current position
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentPosition = exoPlayer.currentPosition.coerceAtLeast(0L)
            delay(500)
        }
    }

    // Auto-hide controls
    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying) {
            delay(Constants.CONTROLS_HIDE_DELAY_MS)
            showControls = false
        }
    }

    // Clear gesture indicator
    LaunchedEffect(gestureIndicator) {
        if (gestureIndicator.isNotEmpty()) {
            delay(800)
            gestureIndicator = ""
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .playerGestures(
                screenWidth = 1080f,
                enabled = true,
                onGesture = { gesture ->
                    when {
                        gesture.isSingleTap -> showControls = !showControls
                        gesture.isDoubleTapLeft -> {
                            val newPos = (exoPlayer.currentPosition - 10_000L).coerceAtLeast(0L)
                            exoPlayer.seekTo(newPos)
                            gestureIndicator = "⏪ 10s"
                        }
                        gesture.isDoubleTapRight -> {
                            val newPos = (exoPlayer.currentPosition + 10_000L)
                                .coerceAtMost(exoPlayer.duration)
                            exoPlayer.seekTo(newPos)
                            gestureIndicator = "⏩ 10s"
                        }
                        gesture.seekDelta != 0L -> {
                            val newPos = (exoPlayer.currentPosition + gesture.seekDelta)
                                .coerceIn(0L, exoPlayer.duration)
                            exoPlayer.seekTo(newPos)
                            gestureIndicator = if (gesture.seekDelta > 0) "▶▶ ${(gesture.seekDelta / 1000)}s"
                            else "◀◀ ${(-gesture.seekDelta / 1000)}s"
                        }
                        gesture.volumeDelta != 0f -> {
                            currentVolume = (currentVolume + gesture.volumeDelta).coerceIn(0f, 1f)
                            audioManager.setStreamVolume(
                                AudioManager.STREAM_MUSIC,
                                (currentVolume * maxVolume).toInt(), 0
                            )
                            gestureIndicator = "🔊 ${(currentVolume * 100).toInt()}%"
                        }
                        gesture.brightnessDelta != 0f -> {
                            currentBrightness = (currentBrightness + gesture.brightnessDelta).coerceIn(0.01f, 1f)
                            val lp = (context as? VideoPlayerActivity)?.window?.attributes
                            lp?.screenBrightness = currentBrightness
                            (context as? VideoPlayerActivity)?.window?.attributes = lp
                            gestureIndicator = "☀️ ${(currentBrightness * 100).toInt()}%"
                        }
                    }
                }
            )
    ) {
        // ExoPlayer View
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Buffering indicator
        if (isBuffering) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Gesture indicator
        if (gestureIndicator.isNotEmpty()) {
            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text(
                    text = gestureIndicator,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }
        }

        // Player Controls Overlay
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            ) {
                // Top bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                        .align(Alignment.TopCenter),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        text = title,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f).padding(start = 4.dp),
                        maxLines = 1
                    )
                    // Speed control
                    TextButton(onClick = {
                        playbackSpeed = when (playbackSpeed) {
                            0.5f -> 0.75f; 0.75f -> 1.0f; 1.0f -> 1.25f
                            1.25f -> 1.5f; 1.5f -> 2.0f; else -> 0.5f
                        }
                        exoPlayer.setPlaybackSpeed(playbackSpeed)
                    }) {
                        Text("${playbackSpeed}x", color = Color.White)
                    }
                }

                // Center play/pause
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    IconButton(
                        onClick = {
                            exoPlayer.seekTo((exoPlayer.currentPosition - 10_000L).coerceAtLeast(0L))
                        }
                    ) {
                        Icon(
                            Icons.Default.Replay10,
                            contentDescription = "Rewind 10s",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
                        }
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            val newPos = (exoPlayer.currentPosition + 10_000L)
                                .coerceAtMost(exoPlayer.duration)
                            exoPlayer.seekTo(newPos)
                        }
                    ) {
                        Icon(
                            Icons.Default.Forward10,
                            contentDescription = "Forward 10s",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                // Bottom seekbar
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(currentPosition.toReadableDuration(), color = Color.White,
                            style = MaterialTheme.typography.labelSmall)
                        Text(duration.toReadableDuration(), color = Color.White,
                            style = MaterialTheme.typography.labelSmall)
                    }
                    Slider(
                        value = if (duration > 0) currentPosition.toFloat() / duration else 0f,
                        onValueChange = { fraction ->
                            val seekTo = (fraction * duration).toLong()
                            exoPlayer.seekTo(seekTo)
                            currentPosition = seekTo
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
