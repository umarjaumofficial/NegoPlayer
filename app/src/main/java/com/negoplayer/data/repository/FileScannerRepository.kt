package com.negoplayer.data.repository

import android.content.Context
import com.negoplayer.data.model.MediaFolder
import com.negoplayer.data.model.MediaItem
import com.negoplayer.data.source.LocalDataSource
import com.negoplayer.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Repository for scanning the file system for media files.
 * Used by the Folders screen to display folder-based browsing.
 */
class FileScannerRepository(context: Context) {

    private val localDataSource = LocalDataSource(context)

    /**
     * Returns all folders that contain video files.
     */
    suspend fun getVideoFolders(): List<MediaFolder> =
        localDataSource.getVideoFolders()

    /**
     * Returns all folders that contain audio files, grouped by folder.
     */
    suspend fun getAudioFolders(): List<MediaFolder> = withContext(Dispatchers.IO) {
        val allAudio = localDataSource.getAudioFiles()
        allAudio.groupBy { it.folderPath }.map { (folderPath, items) ->
            MediaFolder(
                path = folderPath,
                name = folderPath.substringAfterLast('/'),
                audioCount = items.size,
                items = items
            )
        }.sortedBy { it.name }
    }

    /**
     * Returns media items within a specific folder path.
     */
    suspend fun getItemsInFolder(folderPath: String): List<MediaItem> =
        withContext(Dispatchers.IO) {
            val videos = localDataSource.getVideos().filter { it.folderPath == folderPath }
            val audio = localDataSource.getAudioFiles().filter { it.folderPath == folderPath }
            (videos + audio).sortedBy { it.title }
        }

    /**
     * Browses a directory path and returns media files and sub-folders.
     */
    suspend fun browseDirectory(dirPath: String): Pair<List<File>, List<MediaItem>> =
        withContext(Dispatchers.IO) {
            val dir = File(dirPath)
            if (!dir.exists() || !dir.isDirectory) return@withContext Pair(emptyList(), emptyList())

            val subDirs = dir.listFiles()
                ?.filter { it.isDirectory && !it.isHidden }
                ?.sortedBy { it.name }
                ?: emptyList()

            val mediaFiles = dir.listFiles()
                ?.filter { file ->
                    file.isFile && (
                            file.extension.lowercase() in Constants.VIDEO_EXTENSIONS ||
                                    file.extension.lowercase() in Constants.AUDIO_EXTENSIONS
                            )
                }
                ?.sortedBy { it.name }
                ?.map { file ->
                    val isVideo = file.extension.lowercase() in Constants.VIDEO_EXTENSIONS
                    MediaItem(
                        id = file.hashCode().toLong(),
                        title = file.nameWithoutExtension,
                        uri = android.net.Uri.fromFile(file),
                        path = file.absolutePath,
                        duration = 0L,
                        size = file.length(),
                        mimeType = if (isVideo) "video/mp4" else "audio/mpeg",
                        dateAdded = file.lastModified(),
                        dateModified = file.lastModified(),
                        folderName = file.parentFile?.name ?: "",
                        folderPath = file.parent ?: ""
                    )
                } ?: emptyList()

            Pair(subDirs, mediaFiles)
        }
}
