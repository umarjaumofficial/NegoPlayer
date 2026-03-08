package com.negoplayer.data.repository

import android.content.Context
import com.negoplayer.data.model.MediaFolder
import com.negoplayer.data.model.MediaItem
import com.negoplayer.data.source.LocalDataSource
import com.negoplayer.data.source.NetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Main repository that aggregates local and network media sources.
 */
class MediaRepository(context: Context) {

    private val localDataSource = LocalDataSource(context)
    private val networkDataSource = NetworkDataSource()

    fun getVideos(): Flow<List<MediaItem>> = flow {
        emit(localDataSource.getVideos())
    }

    fun getAudioFiles(): Flow<List<MediaItem>> = flow {
        emit(localDataSource.getAudioFiles())
    }

    fun getVideoFolders(): Flow<List<MediaFolder>> = flow {
        emit(localDataSource.getVideoFolders())
    }

    suspend fun searchVideos(query: String): List<MediaItem> =
        localDataSource.searchVideos(query)

    suspend fun searchAudio(query: String): List<MediaItem> =
        localDataSource.searchAudio(query)

    fun createNetworkMediaItem(url: String, title: String = "") =
        networkDataSource.createNetworkMediaItem(url, title)

    fun isSupportedStreamUrl(url: String) =
        networkDataSource.isSupportedStreamUrl(url)
}
