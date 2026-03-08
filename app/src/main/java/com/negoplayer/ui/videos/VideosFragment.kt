package com.negoplayer.ui.videos

import android.Manifest
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.negoplayer.data.model.MediaItem
import com.negoplayer.data.model.MediaUiState
import com.negoplayer.utils.PermissionsUtils
import com.negoplayer.utils.toReadableDuration
import com.negoplayer.viewmodel.SortOrder
import com.negoplayer.viewmodel.VideoViewModel

/**
 * Videos screen showing a grid of all video files on the device.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VideosScreen(
    viewModel: VideoViewModel = viewModel(),
    onPlayVideo: (uri: String, title: String, playlist: List<String>, index: Int) -> Unit
) {
    val permissions = PermissionsUtils.requiredMediaPermissions().toList()
    val permissionsState = rememberMultiplePermissionsState(permissions)

    LaunchedEffect(permissionsState.allPermissionsGranted) {
        if (permissionsState.allPermissionsGranted) viewModel.loadVideos()
    }

    if (!permissionsState.allPermissionsGranted) {
        PermissionRequestScreen(
            onRequestPermission = { permissionsState.launchMultiplePermissionRequest() }
        )
        return
    }

    val videosState by viewModel.videosState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    var showSortMenu by remember { mutableStateOf(false) }
    var isSearchActive by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Bar
        TopAppBar(
            title = {
                if (isSearchActive) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.search(it) },
                        placeholder = { Text("Search videos...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text("Videos")
                }
            },
            actions = {
                IconButton(onClick = { isSearchActive = !isSearchActive; if (!isSearchActive) viewModel.search("") }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
                IconButton(onClick = { showSortMenu = true }) {
                    Icon(Icons.Default.Sort, contentDescription = "Sort")
                }
                DropdownMenu(expanded = showSortMenu, onDismissRequest = { showSortMenu = false }) {
                    listOf(
                        "Name (A-Z)" to SortOrder.NAME_ASC,
                        "Name (Z-A)" to SortOrder.NAME_DESC,
                        "Newest First" to SortOrder.DATE_DESC,
                        "Oldest First" to SortOrder.DATE_ASC,
                        "Largest First" to SortOrder.SIZE_DESC,
                        "Longest First" to SortOrder.DURATION_DESC
                    ).forEach { (label, order) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = { viewModel.setSortOrder(order); showSortMenu = false }
                        )
                    }
                }
            }
        )

        // Content
        if (isSearchActive && searchQuery.isNotBlank()) {
            VideoGrid(
                videos = searchResults,
                onPlayVideo = onPlayVideo,
                allVideos = searchResults
            )
        } else {
            when (val state = videosState) {
                is MediaUiState.Loading -> LoadingScreen()
                is MediaUiState.Empty -> EmptyScreen("No videos found")
                is MediaUiState.Error -> ErrorScreen(state.message) { viewModel.loadVideos() }
                is MediaUiState.Success -> VideoGrid(
                    videos = state.data,
                    onPlayVideo = onPlayVideo,
                    allVideos = state.data
                )
            }
        }
    }
}

@Composable
fun VideoGrid(
    videos: List<MediaItem>,
    allVideos: List<MediaItem>,
    onPlayVideo: (uri: String, title: String, playlist: List<String>, index: Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(videos, key = { it.id }) { video ->
            VideoCard(
                video = video,
                onClick = {
                    val playlist = allVideos.map { it.uri.toString() }
                    val index = allVideos.indexOf(video)
                    onPlayVideo(video.uri.toString(), video.displayTitle, playlist, index)
                }
            )
        }
    }
}

@Composable
fun VideoCard(video: MediaItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(video.uri)
                        .crossfade(true)
                        .build(),
                    contentDescription = video.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                )
                // Duration badge
                Text(
                    text = video.duration.toReadableDuration(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
                // Play icon overlay
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.Center)
                )
            }
            // Title
            Text(
                text = video.displayTitle,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
fun PermissionRequestScreen(onRequestPermission: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "Storage Permission Required",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "NegoPlayer needs access to your media files to display and play them.",
                style = MaterialTheme.typography.bodyMedium
            )
            Button(onClick = onRequestPermission) {
                Text("Grant Permission")
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun EmptyScreen(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(message, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}
