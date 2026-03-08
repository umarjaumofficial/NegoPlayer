package com.negoplayer.data.source

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.negoplayer.data.model.MediaItem
import com.negoplayer.data.model.MediaFolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Local data source that queries the device MediaStore for video and audio files.
 */
class LocalDataSource(private val context: Context) {

    /**
     * Scans and returns all video files from the device MediaStore.
     */
    suspend fun getVideos(): List<MediaItem> = withContext(Dispatchers.IO) {
        val videos = mutableListOf<MediaItem>()
        val projection = buildVideoProjection()
        val sortOrder = "${MediaStore.Video.Media.DATE_MODIFIED} DESC"

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        context.contentResolver.query(
            collection, projection, null, null, sortOrder
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                parseVideoCursor(cursor)?.let { videos.add(it) }
            }
        }
        videos
    }

    /**
     * Scans and returns all audio files from the device MediaStore.
     */
    suspend fun getAudioFiles(): List<MediaItem> = withContext(Dispatchers.IO) {
        val audios = mutableListOf<MediaItem>()
        val projection = buildAudioProjection()
        val sortOrder = "${MediaStore.Audio.Media.DATE_MODIFIED} DESC"

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        context.contentResolver.query(
            collection, projection, null, null, sortOrder
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                parseAudioCursor(cursor)?.let { audios.add(it) }
            }
        }
        audios
    }

    /**
     * Groups media items into folders.
     */
    suspend fun getVideoFolders(): List<MediaFolder> = withContext(Dispatchers.IO) {
        val allVideos = getVideos()
        allVideos.groupBy { it.folderPath }.map { (folderPath, items) ->
            MediaFolder(
                path = folderPath,
                name = folderPath.substringAfterLast('/'),
                videoCount = items.size,
                items = items
            )
        }.sortedBy { it.name }
    }

    /**
     * Search videos by title.
     */
    suspend fun searchVideos(query: String): List<MediaItem> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        val projection = buildVideoProjection()
        val selection = "${MediaStore.Video.Media.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf("%$query%")
        val videos = mutableListOf<MediaItem>()

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        context.contentResolver.query(
            collection, projection, selection, selectionArgs, null
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                parseVideoCursor(cursor)?.let { videos.add(it) }
            }
        }
        videos
    }

    /**
     * Search audio files by title or artist.
     */
    suspend fun searchAudio(query: String): List<MediaItem> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        val projection = buildAudioProjection()
        val selection = "${MediaStore.Audio.Media.DISPLAY_NAME} LIKE ? OR ${MediaStore.Audio.Media.ARTIST} LIKE ?"
        val selectionArgs = arrayOf("%$query%", "%$query%")
        val audios = mutableListOf<MediaItem>()

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        context.contentResolver.query(
            collection, projection, selection, selectionArgs, null
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                parseAudioCursor(cursor)?.let { audios.add(it) }
            }
        }
        audios
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private fun buildVideoProjection(): Array<String> = arrayOf(
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.DISPLAY_NAME,
        MediaStore.Video.Media.DATA,
        MediaStore.Video.Media.DURATION,
        MediaStore.Video.Media.SIZE,
        MediaStore.Video.Media.MIME_TYPE,
        MediaStore.Video.Media.DATE_ADDED,
        MediaStore.Video.Media.DATE_MODIFIED,
        MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
        MediaStore.Video.Media.BUCKET_ID,
        MediaStore.Video.Media.WIDTH,
        MediaStore.Video.Media.HEIGHT
    )

    private fun buildAudioProjection(): Array<String> = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.SIZE,
        MediaStore.Audio.Media.MIME_TYPE,
        MediaStore.Audio.Media.DATE_ADDED,
        MediaStore.Audio.Media.DATE_MODIFIED,
        MediaStore.Audio.Media.BUCKET_DISPLAY_NAME,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.TRACK
    )

    private fun parseVideoCursor(cursor: Cursor): MediaItem? {
        return try {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)) ?: ""
            val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)) ?: ""
            val duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
            val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE))
            val mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)) ?: "video/mp4"
            val dateAdded = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED))
            val dateModified = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED))
            val folderName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)) ?: ""
            val width = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH))
            val height = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT))

            val contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
            val folderPath = path.substringBeforeLast('/')

            MediaItem(
                id = id,
                title = name.substringBeforeLast('.'),
                uri = contentUri,
                path = path,
                duration = duration,
                size = size,
                mimeType = mimeType,
                dateAdded = dateAdded * 1000,
                dateModified = dateModified * 1000,
                folderName = folderName,
                folderPath = folderPath,
                width = width,
                height = height
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun parseAudioCursor(cursor: Cursor): MediaItem? {
        return try {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)) ?: ""
            val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)) ?: ""
            val duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
            val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE))
            val mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)) ?: "audio/mpeg"
            val dateAdded = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED))
            val dateModified = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED))
            val folderName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.BUCKET_DISPLAY_NAME)) ?: ""
            val artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)) ?: ""
            val album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)) ?: ""
            val albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
            val track = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK))

            val contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
            val folderPath = path.substringBeforeLast('/')

            MediaItem(
                id = id,
                title = name.substringBeforeLast('.'),
                uri = contentUri,
                path = path,
                duration = duration,
                size = size,
                mimeType = mimeType,
                dateAdded = dateAdded * 1000,
                dateModified = dateModified * 1000,
                folderName = folderName,
                folderPath = folderPath,
                artist = artist,
                album = album,
                albumId = albumId,
                trackNumber = track
            )
        } catch (e: Exception) {
            null
        }
    }
}
