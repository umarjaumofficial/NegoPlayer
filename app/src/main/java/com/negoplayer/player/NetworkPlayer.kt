package com.negoplayer.player

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi

/**
 * Helper for creating network stream media items for Media3.
 * Supports HTTP, HTTPS, HLS (m3u8), DASH (mpd), and RTSP streams.
 */
@UnstableApi
object NetworkPlayer {

    /**
     * Creates a Media3 MediaItem from a network URL with proper MIME type detection.
     */
    fun buildMediaItem(url: String, title: String = ""): MediaItem {
        val uri = Uri.parse(url)
        val mimeType = detectMimeType(url)

        val displayTitle = title.ifBlank {
            url.substringAfterLast('/').substringBefore('?').let {
                if (it.isBlank()) "Network Stream" else it.substringBeforeLast('.')
            }
        }

        val builder = MediaItem.Builder()
            .setUri(uri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(displayTitle)
                    .build()
            )

        if (mimeType != null) {
            builder.setMimeType(mimeType)
        }

        return builder.build()
    }

    /**
     * Detects the MIME type of a stream URL by inspecting its format/extension.
     */
    fun detectMimeType(url: String): String? {
        val lower = url.lowercase()
        return when {
            lower.contains(".m3u8") || lower.contains("hls") -> MimeTypes.APPLICATION_M3U8
            lower.contains(".mpd") || lower.contains("dash") -> MimeTypes.APPLICATION_MPD
            lower.startsWith("rtsp://") -> MimeTypes.APPLICATION_RTSP
            lower.contains(".mp4") -> MimeTypes.VIDEO_MP4
            lower.contains(".webm") -> MimeTypes.VIDEO_WEBM
            lower.contains(".mkv") -> MimeTypes.VIDEO_MATROSKA
            lower.contains(".mp3") -> MimeTypes.AUDIO_MPEG
            lower.contains(".aac") -> MimeTypes.AUDIO_AAC
            lower.contains(".ogg") -> MimeTypes.AUDIO_OGG
            lower.contains(".opus") -> MimeTypes.AUDIO_OPUS
            else -> null  // Let ExoPlayer auto-detect
        }
    }

    /**
     * Returns true if the URL is a live stream.
     */
    fun isLikelyLiveStream(url: String): Boolean {
        val lower = url.lowercase()
        return lower.contains("live") ||
                lower.contains("stream") ||
                lower.contains("rtsp://") ||
                lower.contains("rtmp://") ||
                lower.contains(".m3u8")
    }

    /**
     * Returns supported stream URL prefixes.
     */
    fun getSupportedPrefixes(): List<String> = listOf(
        "http://", "https://", "rtsp://", "rtmp://"
    )
}
