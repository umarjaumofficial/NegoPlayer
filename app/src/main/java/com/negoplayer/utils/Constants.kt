package com.negoplayer.utils

/**
 * App-wide constants used throughout NegoPlayer.
 */
object Constants {

    // Notification
    const val PLAYBACK_NOTIFICATION_CHANNEL_ID = "negoplayer_playback"
    const val PLAYBACK_NOTIFICATION_ID = 1001

    // Intent extras
    const val EXTRA_MEDIA_URI = "extra_media_uri"
    const val EXTRA_MEDIA_TITLE = "extra_media_title"
    const val EXTRA_MEDIA_POSITION = "extra_media_position"
    const val EXTRA_PLAYLIST_INDEX = "extra_playlist_index"

    // DataStore keys
    const val PREFS_NAME = "negoplayer_prefs"
    const val PREF_THEME = "pref_theme"
    const val PREF_SUBTITLE_SIZE = "pref_subtitle_size"
    const val PREF_DEFAULT_AUDIO_TRACK = "pref_default_audio_track"
    const val PREF_REMEMBER_POSITION = "pref_remember_position"
    const val PREF_GESTURE_ENABLED = "pref_gesture_enabled"
    const val PREF_DOUBLE_TAP_SEEK = "pref_double_tap_seek"
    const val PREF_BRIGHTNESS = "pref_brightness"
    const val PREF_VOLUME = "pref_volume"

    // Playback
    const val SEEK_INCREMENT_MS = 10_000L  // 10 seconds
    const val LONG_PRESS_SEEK_MS = 60_000L  // 1 minute
    const val PLAYBACK_SPEED_STEP = 0.25f

    // Media types
    val VIDEO_MIME_TYPES = listOf(
        "video/mp4", "video/mkv", "video/x-matroska", "video/avi",
        "video/webm", "video/3gp", "video/flv", "video/mov",
        "video/wmv", "video/mpeg"
    )
    val AUDIO_MIME_TYPES = listOf(
        "audio/mpeg", "audio/mp3", "audio/flac", "audio/ogg",
        "audio/wav", "audio/aac", "audio/x-ms-wma", "audio/opus"
    )

    // Supported extensions
    val VIDEO_EXTENSIONS = setOf(
        "mp4", "mkv", "avi", "webm", "3gp", "flv", "mov", "wmv",
        "mpeg", "mpg", "ts", "m4v", "divx", "xvid", "rmvb", "rm"
    )
    val AUDIO_EXTENSIONS = setOf(
        "mp3", "flac", "ogg", "wav", "aac", "wma", "opus", "m4a",
        "alac", "aiff", "ape", "dsd"
    )

    // UI
    const val CONTROLS_HIDE_DELAY_MS = 3000L
    const val GESTURE_SENSITIVITY = 0.01f
}
