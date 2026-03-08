package com.negoplayer.player

import android.content.Context
import android.net.Uri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem as Media3Item
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.negoplayer.data.model.MediaItem
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Manages the Media3 ExoPlayer instance for NegoPlayer.
 * Provides a centralized, reusable player with proper audio focus handling.
 */
@UnstableApi
class ExoPlayerManager(private val context: Context) {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val trackSelector = DefaultTrackSelector(context).apply {
        setParameters(buildUponParameters().setPreferredAudioLanguage("en"))
    }

    /**
     * Creates a new ExoPlayer instance configured for media playback.
     */
    fun createPlayer(): ExoPlayer {
        val httpDataSourceFactory = OkHttpDataSource.Factory(okHttpClient)
        val dataSourceFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)
        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
            .build()

        return ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .setTrackSelector(trackSelector)
            .setAudioAttributes(audioAttributes, /* handleAudioFocus= */ true)
            .setHandleAudioBecomingNoisy(true)
            .build()
    }

    /**
     * Converts a NegoPlayer MediaItem to a Media3 MediaItem.
     */
    fun toMedia3Item(item: MediaItem): Media3Item {
        val metadata = MediaMetadata.Builder()
            .setTitle(item.title)
            .setArtist(item.artist.ifBlank { null })
            .setAlbumTitle(item.album.ifBlank { null })
            .build()

        return Media3Item.Builder()
            .setUri(item.uri)
            .setMediaId(item.id.toString())
            .setMediaMetadata(metadata)
            .build()
    }

    /**
     * Converts a list of MediaItems to Media3 MediaItems for playlist playback.
     */
    fun toMedia3Playlist(items: List<MediaItem>): List<Media3Item> =
        items.map { toMedia3Item(it) }

    /**
     * Creates a Media3 MediaItem directly from a URI (for external opens).
     */
    fun fromUri(uri: Uri, title: String = ""): Media3Item {
        return Media3Item.Builder()
            .setUri(uri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title.ifBlank { uri.lastPathSegment ?: "Media" })
                    .build()
            )
            .build()
    }

    /**
     * Sets up subtitle/caption preferences on a player.
     */
    fun configureSubtitles(player: ExoPlayer, enabled: Boolean, textSize: Float = 1.0f) {
        val params = player.trackSelectionParameters
        if (enabled) {
            player.trackSelectionParameters = params.buildUpon()
                .setPreferredTextLanguage("en")
                .setIgnoredTextSelectionFlags(0)
                .build()
        } else {
            player.trackSelectionParameters = params.buildUpon()
                .setIgnoredTextSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                .build()
        }
    }
}
