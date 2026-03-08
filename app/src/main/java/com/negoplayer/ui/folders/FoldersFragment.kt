package com.negoplayer.ui.folders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.negoplayer.data.model.MediaFolder
import com.negoplayer.data.model.MediaItem
import com.negoplayer.data.model.MediaUiState
import com.negoplayer.ui.videos.EmptyScreen
import com.negoplayer.ui.videos.ErrorScreen
import com.negoplayer.ui.videos.LoadingScreen
import com.negoplayer.ui.videos.PermissionRequestScreen
import com.negoplayer.ui.videos.VideoCard
import com.negoplayer.utils.PermissionsUtils
import com.negoplayer.viewmodel.FoldersViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FoldersScreen(
    viewModel: FoldersViewModel = viewModel(),
    onPlayVideo: (uri: String, title: String, playlist: List<String>, index: Int) -> Unit
) {
    val permissions = PermissionsUtils.requiredMediaPermissions().toList()
    val permissionsState = rememberMultiplePermissionsState(permissions)

    LaunchedEffect(permissionsState.allPermissionsGranted) {
        if (permissionsState.allPermissionsGranted) viewModel.loadFolders()
    }

    if (!permissionsState.allPermissionsGranted) {
        PermissionRequestScreen(onRequestPermission = { permissionsState.launchMultiplePermissionRequest() })
        return
    }

    val foldersState by viewModel.foldersState.collectAsState()
    val selectedFolder by viewModel.selectedFolder.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = selectedFolder?.name ?: "Folders",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                if (selectedFolder != null) {
                    IconButton(onClick = { viewModel.clearSelection() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            }
        )

        if (selectedFolder != null) {
            FolderContentScreen(
                folder = selectedFolder!!,
                onPlayVideo = onPlayVideo
            )
        } else {
            when (val state = foldersState) {
                is MediaUiState.Loading -> LoadingScreen()
                is MediaUiState.Empty -> EmptyScreen("No media folders found")
                is MediaUiState.Error -> ErrorScreen(state.message) { viewModel.loadFolders() }
                is MediaUiState.Success -> FoldersList(
                    folders = state.data,
                    onFolderClick = { viewModel.selectFolder(it) }
                )
            }
        }
    }
}

@Composable
fun FoldersList(
    folders: List<MediaFolder>,
    onFolderClick: (MediaFolder) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(folders, key = { it.path }) { folder ->
            FolderItem(folder = folder, onClick = { onFolderClick(folder) })
            HorizontalDivider()
        }
    }
}

@Composable
fun FolderItem(folder: MediaFolder, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(folder.name) },
        supportingContent = {
            Text(buildString {
                if (folder.videoCount > 0) append("${folder.videoCount} videos")
                if (folder.videoCount > 0 && folder.audioCount > 0) append(", ")
                if (folder.audioCount > 0) append("${folder.audioCount} audio")
            })
        },
        leadingContent = {
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
        },
        trailingContent = {
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
fun FolderContentScreen(
    folder: MediaFolder,
    onPlayVideo: (uri: String, title: String, playlist: List<String>, index: Int) -> Unit
) {
    val videoItems = folder.items.filter { it.isVideo }
    val audioItems = folder.items.filter { it.isAudio }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        if (videoItems.isNotEmpty()) {
            item {
                Text(
                    text = "Videos (${videoItems.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            items(videoItems, key = { it.id }) { video ->
                VideoListItem(
                    video = video,
                    onClick = {
                        val playlist = videoItems.map { it.uri.toString() }
                        val index = videoItems.indexOf(video)
                        onPlayVideo(video.uri.toString(), video.displayTitle, playlist, index)
                    }
                )
            }
        }
        if (audioItems.isNotEmpty()) {
            item {
                Text(
                    text = "Audio (${audioItems.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            items(audioItems, key = { it.id }) { audio ->
                VideoListItem(
                    video = audio,
                    onClick = {}
                )
            }
        }
    }
}

@Composable
fun VideoListItem(video: MediaItem, onClick: () -> Unit) {
    ListItem(
        headlineContent = {
            Text(video.displayTitle, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        supportingContent = {
            Text(video.path, maxLines = 1, overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall)
        },
        leadingContent = {
            Icon(
                imageVector = if (video.isVideo) Icons.Default.VideoFile else Icons.Default.AudioFile,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
    HorizontalDivider(modifier = Modifier.padding(start = 72.dp))
}
