package com.negoplayer.data.source

import android.net.Uri
import com.negoplayer.data.model.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

/**
 * Data source for network-based media streams.
 * Supports HTTP, HTTPS, RTSP, and HLS streams.
 */
class NetworkDataSource {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    /**
     * Validates if a URL is a playable stream.
     */
    suspend fun validateStreamUrl(url: String): Boolean = withContext(Dispatchers.IO) {
        try {
            if (url.startsWith("rtsp://") || url.startsWith("rtmp://")) {
                return@withContext isValidUri(url)
            }
            val request = Request.Builder().url(url).head().build()
            val response = client.newCall(request).execute()
            response.use { it.isSuccessful || it.code == 302 || it.code == 301 }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Creates a MediaItem from a network URL for playback.
     */
    fun createNetworkMediaItem(url: String, title: String = ""): MediaItem {
        val displayTitle = title.ifBlank {
            url.substringAfterLast('/').substringBefore('?').let {
                if (it.isBlank()) "Network Stream" else it
            }
        }
        val mimeType = when {
            url.endsWith(".m3u8") || url.contains("hls") -> "application/x-mpegURL"
            url.endsWith(".mpd") || url.contains("dash") -> "application/dash+xml"
            url.startsWith("rtsp://") -> "video/rtsp"
            url.endsWith(".mp4") -> "video/mp4"
            url.endsWith(".mkv") -> "video/x-matroska"
            url.endsWith(".mp3") -> "audio/mpeg"
            url.endsWith(".aac") -> "audio/aac"
            else -> "video/mp4"
        }
        return MediaItem(
            id = url.hashCode().toLong(),
            title = displayTitle,
            uri = Uri.parse(url),
            path = url,
            duration = 0L,
            size = 0L,
            mimeType = mimeType,
            dateAdded = System.currentTimeMillis(),
            dateModified = System.currentTimeMillis(),
            folderName = "Network",
            folderPath = ""
        )
    }

    /**
     * Checks if supported streaming protocols are in the URL.
     */
    fun isSupportedStreamUrl(url: String): Boolean {
        val lower = url.lowercase()
        return lower.startsWith("http://") ||
                lower.startsWith("https://") ||
                lower.startsWith("rtsp://") ||
                lower.startsWith("rtmp://") ||
                lower.endsWith(".m3u8") ||
                lower.endsWith(".mpd")
    }

    private fun isValidUri(uriString: String): Boolean {
        return try {
            val uri = Uri.parse(uriString)
            uri.scheme != null && uri.host != null
        } catch (e: Exception) {
            false
        }
    }
}
