package com.negoplayer.ui.music

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.negoplayer.data.model.MediaItem as NegoMediaItem
import com.negoplayer.data.model.MediaUiState
import com.negoplayer.ui.videos.EmptyScreen
import com.negoplayer.ui.videos.ErrorScreen
import com.negoplayer.ui.videos.LoadingScreen
import com.negoplayer.ui.videos.PermissionRequestScreen
import com.negoplayer.utils.PermissionsUtils
import com.negoplayer.utils.toReadableDuration
import com.negoplayer.viewmodel.MusicGroupBy
import com.negoplayer.viewmodel.MusicViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalPermissionsApi::class, UnstableApi::class)
@Composable
fun MusicScreen(viewModel: MusicViewModel = viewModel()) {
    val context = LocalContext.current
    val permissions = PermissionsUtils.audioPermissions().toList()
    val permissionsState = rememberMultiplePermissionsState(permissions)

    LaunchedEffect(permissionsState.allPermissionsGranted) {
        if (permissionsState.allPermissionsGranted) viewModel.loadMusic()
    }

    if (!permissionsState.allPermissionsGranted) {
        PermissionRequestScreen(onRequestPermission = { permissionsState.launchMultiplePermissionRequest() })
        return
    }

    val musicState by viewModel.musicState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentIndex by viewModel.currentTrackIndex.collectAsState()
    var isSearchActive by remember { mutableStateOf(false) }

    // Simple inline ExoPlayer for audio
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    DisposableEffect(Unit) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                viewModel.setPlaying(playing)
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    fun playTrack(tracks: List<NegoMediaItem>, index: Int) {
        if (index < 0 || index >= tracks.size) return
        viewModel.setCurrentTrack(index)
        val mediaItems = tracks.map { MediaItem.fromUri(it.uri) }
        exoPlayer.setMediaItems(mediaItems, index, 0L)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                if (isSearchActive) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.search(it) },
                        placeholder = { Text("Search music...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text("Music")
                }
            },
            actions = {
                IconButton(onClick = { isSearchActive = !isSearchActive; if (!isSearchActive) viewModel.search("") }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            }
        )

        val tracks = if (isSearchActive && searchQuery.isNotBlank()) {
            searchResults
        } else {
            when (val state = musicState) {
                is MediaUiState.Success -> state.data
                else -> emptyList()
            }
        }

        when {
            musicState is MediaUiState.Loading && !isSearchActive -> LoadingScreen()
            musicState is MediaUiState.Empty && !isSearchActive -> EmptyScreen("No music found")
            musicState is MediaUiState.Error -> ErrorScreen(
                (musicState as MediaUiState.Error).message
            ) { viewModel.loadMusic() }
            else -> {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    itemsIndexed(tracks, key = { _, item -> item.id }) { index, track ->
                        MusicTrackItem(
                            track = track,
                            isPlaying = isPlaying && currentIndex == index,
                            onClick = { playTrack(tracks, index) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(start = 72.dp))
                    }
                }

                // Mini player at bottom
                if (isPlaying || currentIndex < tracks.size) {
                    MiniMusicPlayer(
                        track = if (currentIndex < tracks.size) tracks[currentIndex] else null,
                        isPlaying = isPlaying,
                        onPlayPause = { if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play() },
                        onNext = { playTrack(tracks, currentIndex + 1) },
                        onPrev = { playTrack(tracks, (currentIndex - 1).coerceAtLeast(0)) }
                    )
                }
            }
        }
    }
}

@Composable
fun MusicTrackItem(
    track: NegoMediaItem,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                text = track.displayTitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        },
        supportingContent = {
            Text(
                text = buildString {
                    if (track.artist.isNotBlank()) append(track.artist)
                    if (track.artist.isNotBlank() && track.album.isNotBlank()) append(" · ")
                    if (track.album.isNotBlank()) append(track.album)
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingContent = {
            Box(modifier = Modifier.size(48.dp)) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("content://media/external/audio/albumart/${track.albumId}")
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp))
                )
                if (isPlaying) {
                    Icon(
                        Icons.Default.VolumeUp,
                        contentDescription = "Playing",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        },
        trailingContent = {
            Text(
                text = track.duration.toReadableDuration(),
                style = MaterialTheme.typography.labelSmall
            )
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
fun MiniMusicPlayer(
    track: NegoMediaItem?,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit
) {
    if (track == null) return
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.displayTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (track.artist.isNotBlank()) {
                    Text(
                        text = track.artist,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            IconButton(onClick = onPrev) {
                Icon(Icons.Default.SkipPrevious, contentDescription = "Previous")
            }
            IconButton(onClick = onPlayPause) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }
            IconButton(onClick = onNext) {
                Icon(Icons.Default.SkipNext, contentDescription = "Next")
            }
        }
    }
}
