package com.negoplayer.data.model

import android.net.Uri

/**
 * Represents a single media item (video or audio) in NegoPlayer.
 */
data class MediaItem(
    val id: Long,
    val title: String,
    val uri: Uri,
    val path: String,
    val duration: Long,        // milliseconds
    val size: Long,            // bytes
    val mimeType: String,
    val dateAdded: Long,       // Unix timestamp
    val dateModified: Long,    // Unix timestamp
    val folderName: String,
    val folderPath: String,
    // Video-specific
    val width: Int = 0,
    val height: Int = 0,
    // Audio-specific
    val artist: String = "",
    val album: String = "",
    val albumId: Long = 0L,
    val trackNumber: Int = 0
) {
    val isVideo: Boolean get() = mimeType.startsWith("video/")
    val isAudio: Boolean get() = mimeType.startsWith("audio/")

    val resolution: String get() = if (width > 0 && height > 0) "${width}x${height}" else ""

    val displayTitle: String get() = title.ifBlank {
        path.substringAfterLast('/').substringBeforeLast('.')
    }
}

/**
 * Represents the playback state of a media item (for remembering position).
 */
data class MediaPlaybackState(
    val mediaId: Long,
    val position: Long,   // milliseconds
    val timestamp: Long   // when this was saved
)
