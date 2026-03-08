/*
 * NegoPlayer - Advanced Android Media Platform
 * Author: Muhammad Umar
 * Project: NegoPlayer
 * File: LibraryViewModel.kt
 */

package com.negoplayer.app.ui.screens.library

import android.app.Application
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class LibraryUiState(
    val mediaItems: List<MediaItem> = emptyList(),
    val allItems: List<MediaItem> = emptyList(),
    val isLoading: Boolean = false,
    val sortOrder: SortOrder = SortOrder.DATE_DESC,
    val error: String? = null
)

@HiltViewModel
class LibraryViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    fun scanMedia() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val items = scanMediaStore()
            val sorted = sortItems(items, _uiState.value.sortOrder)
            _uiState.value = _uiState.value.copy(
                mediaItems = sorted,
                allItems = sorted,
                isLoading = false
            )
        }
    }

    fun search(query: String) {
        val allItems = _uiState.value.allItems
        val filtered = if (query.isBlank()) allItems
        else allItems.filter { it.displayName.contains(query, ignoreCase = true) }
        _uiState.value = _uiState.value.copy(mediaItems = filtered)
    }

    fun setSortOrder(order: SortOrder) {
        val sorted = sortItems(_uiState.value.allItems, order)
        _uiState.value = _uiState.value.copy(mediaItems = sorted, sortOrder = order)
    }

    private fun sortItems(items: List<MediaItem>, order: SortOrder): List<MediaItem> {
        return when (order) {
            SortOrder.NAME_ASC -> items.sortedBy { it.displayName }
            SortOrder.NAME_DESC -> items.sortedByDescending { it.displayName }
            SortOrder.DATE_DESC -> items
            SortOrder.DATE_ASC -> items.reversed()
            SortOrder.SIZE_DESC -> items.sortedByDescending { parseSizeToBytes(it.size) }
            SortOrder.SIZE_ASC -> items.sortedBy { parseSizeToBytes(it.size) }
            SortOrder.DURATION_DESC -> items.sortedByDescending { it.duration }
        }
    }

    private fun parseSizeToBytes(size: String): Long {
        return try {
            val parts = size.trim().split(" ")
            val value = parts[0].toDouble()
            when (parts.getOrNull(1)?.uppercase()) {
                "KB" -> (value * 1024).toLong()
                "MB" -> (value * 1024 * 1024).toLong()
                "GB" -> (value * 1024 * 1024 * 1024).toLong()
                else -> value.toLong()
            }
        } catch (e: Exception) { 0L }
    }

    private suspend fun scanMediaStore(): List<MediaItem> = withContext(Dispatchers.IO) {
        val items = mutableListOf<MediaItem>()
        val context = getApplication<Application>()

        val videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.MIME_TYPE
        )

        try {
            context.contentResolver.query(
                videoUri,
                projection,
                null,
                null,
                "${MediaStore.Video.Media.DATE_MODIFIED} DESC"
            )?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val name = cursor.getString(nameCol) ?: "Unknown"
                    val path = cursor.getString(dataCol) ?: ""
                    val size = cursor.getLong(sizeCol)
                    val duration = cursor.getLong(durationCol)
                    val mime = cursor.getString(mimeCol) ?: "video/*"

                    val contentUri = ContentUris.withAppendedId(videoUri, id).toString()
                    val thumbnailUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id
                    ).toString()

                    items.add(
                        MediaItem(
                            id = id,
                            uri = contentUri,
                            displayName = name.substringBeforeLast("."),
                            path = path.substringBeforeLast("/"),
                            size = formatFileSize(size),
                            duration = duration,
                            formattedDuration = formatDuration(duration),
                            thumbnailUri = thumbnailUri,
                            mimeType = mime
                        )
                    )
                }
            }
        } catch (e: Exception) {
            // Handle permission errors gracefully
        }

        items
    }

    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes >= 1024L * 1024 * 1024 -> "%.1f GB".format(bytes / (1024.0 * 1024 * 1024))
            bytes >= 1024L * 1024 -> "%.1f MB".format(bytes / (1024.0 * 1024))
            bytes >= 1024L -> "%.1f KB".format(bytes / 1024.0)
            else -> "$bytes B"
        }
    }

    private fun formatDuration(ms: Long): String {
        val totalSec = ms / 1000
        val h = totalSec / 3600
        val m = (totalSec % 3600) / 60
        val s = totalSec % 60
        return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
    }
}
