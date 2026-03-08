package com.negoplayer.data.repository

import com.negoplayer.data.model.MediaItem
import com.negoplayer.data.source.NetworkDataSource

/**
 * Repository for network/streaming media sources.
 */
class NetworkRepository {

    private val networkDataSource = NetworkDataSource()

    suspend fun validateAndCreateItem(url: String, title: String): Result<MediaItem> {
        return try {
            if (!networkDataSource.isSupportedStreamUrl(url)) {
                return Result.failure(IllegalArgumentException("Unsupported stream URL format"))
            }
            val item = networkDataSource.createNetworkMediaItem(url, title)
            Result.success(item)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isSupportedUrl(url: String): Boolean =
        networkDataSource.isSupportedStreamUrl(url)
}
