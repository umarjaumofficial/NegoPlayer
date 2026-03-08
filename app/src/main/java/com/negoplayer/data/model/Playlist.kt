package com.negoplayer.data.model

/**
 * Represents a playlist of media items.
 */
data class Playlist(
    val id: Long,
    val name: String,
    val items: List<MediaItem> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis()
) {
    val totalDuration: Long get() = items.sumOf { it.duration }
    val itemCount: Int get() = items.size
}

/**
 * Represents a folder containing media files.
 */
data class MediaFolder(
    val path: String,
    val name: String,
    val videoCount: Int = 0,
    val audioCount: Int = 0,
    val items: List<MediaItem> = emptyList()
) {
    val totalCount: Int get() = videoCount + audioCount
}

/**
 * Sealed class representing UI state for media loading.
 */
sealed class MediaUiState<out T> {
    object Loading : MediaUiState<Nothing>()
    data class Success<T>(val data: T) : MediaUiState<T>()
    data class Error(val message: String) : MediaUiState<Nothing>()
    object Empty : MediaUiState<Nothing>()
}

/**
 * Represents the current playback info shown in the mini-player.
 */
data class PlaybackInfo(
    val mediaItem: MediaItem? = null,
    val isPlaying: Boolean = false,
    val position: Long = 0L,
    val duration: Long = 0L
)
