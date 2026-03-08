package com.negoplayer.player

import android.net.Uri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import java.io.File

/**
 * Manages subtitle/caption tracks for NegoPlayer.
 * Handles auto-detection of subtitle files alongside video files.
 */
@UnstableApi
object SubtitleManager {

    private val SUBTITLE_EXTENSIONS = setOf("srt", "ass", "ssa", "vtt", "ttml", "sub")

    /**
     * Finds a subtitle file alongside the video file, if one exists.
     * Looks for files with the same name but different extensions.
     */
    fun findSubtitleFile(videoPath: String): File? {
        val videoFile = File(videoPath)
        val baseName = videoFile.nameWithoutExtension
        val dir = videoFile.parentFile ?: return null

        return SUBTITLE_EXTENSIONS
            .map { ext -> File(dir, "$baseName.$ext") }
            .firstOrNull { it.exists() }
    }

    /**
     * Builds a Media3 subtitle configuration for a subtitle file.
     */
    fun buildSubtitleConfig(subtitleFile: File, language: String = "und"): MediaItem.SubtitleConfiguration {
        val mimeType = when (subtitleFile.extension.lowercase()) {
            "srt" -> "application/x-subrip"
            "ass", "ssa" -> "text/x-ssa"
            "vtt" -> "text/vtt"
            "ttml" -> "application/ttml+xml"
            "sub" -> "application/x-subrip"
            else -> "application/x-subrip"
        }
        return MediaItem.SubtitleConfiguration.Builder(Uri.fromFile(subtitleFile))
            .setMimeType(mimeType)
            .setLanguage(language)
            .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
            .build()
    }

    /**
     * Builds a Media3 subtitle configuration from a URI.
     */
    fun buildSubtitleConfig(uri: Uri, mimeType: String = "application/x-subrip",
                            language: String = "und"): MediaItem.SubtitleConfiguration {
        return MediaItem.SubtitleConfiguration.Builder(uri)
            .setMimeType(mimeType)
            .setLanguage(language)
            .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
            .build()
    }

    /**
     * Returns true if the file extension is a supported subtitle format.
     */
    fun isSupportedSubtitle(extension: String): Boolean =
        extension.lowercase() in SUBTITLE_EXTENSIONS

    /**
     * Converts subtitle size preference (1-5 scale) to a Compose text scale multiplier.
     */
    fun sizeToScale(sizeLevel: Int): Float = when (sizeLevel) {
        1 -> 0.75f
        2 -> 0.90f
        3 -> 1.00f  // Default
        4 -> 1.25f
        5 -> 1.50f
        else -> 1.00f
    }
}
