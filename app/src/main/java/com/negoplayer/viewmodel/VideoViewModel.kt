package com.negoplayer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.negoplayer.data.model.MediaItem
import com.negoplayer.data.model.MediaUiState
import com.negoplayer.data.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Videos screen and video player.
 */
class VideoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MediaRepository(application)

    private val _videosState = MutableStateFlow<MediaUiState<List<MediaItem>>>(MediaUiState.Loading)
    val videosState: StateFlow<MediaUiState<List<MediaItem>>> = _videosState.asStateFlow()

    private val _searchResults = MutableStateFlow<List<MediaItem>>(emptyList())
    val searchResults: StateFlow<List<MediaItem>> = _searchResults.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _currentPlaylist = MutableStateFlow<List<MediaItem>>(emptyList())
    val currentPlaylist: StateFlow<List<MediaItem>> = _currentPlaylist.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.DATE_DESC)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    init {
        loadVideos()
    }

    fun loadVideos() {
        viewModelScope.launch {
            _videosState.value = MediaUiState.Loading
            try {
                repository.getVideos().collect { videos ->
                    _videosState.value = if (videos.isEmpty()) {
                        MediaUiState.Empty
                    } else {
                        _currentPlaylist.value = videos
                        MediaUiState.Success(sortVideos(videos, _sortOrder.value))
                    }
                }
            } catch (e: Exception) {
                _videosState.value = MediaUiState.Error(e.message ?: "Failed to load videos")
            }
        }
    }

    fun search(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            _searchResults.value = repository.searchVideos(query)
        }
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
        val currentState = _videosState.value
        if (currentState is MediaUiState.Success) {
            _videosState.value = MediaUiState.Success(sortVideos(currentState.data, order))
        }
    }

    private fun sortVideos(videos: List<MediaItem>, order: SortOrder): List<MediaItem> =
        when (order) {
            SortOrder.NAME_ASC -> videos.sortedBy { it.title.lowercase() }
            SortOrder.NAME_DESC -> videos.sortedByDescending { it.title.lowercase() }
            SortOrder.DATE_ASC -> videos.sortedBy { it.dateModified }
            SortOrder.DATE_DESC -> videos.sortedByDescending { it.dateModified }
            SortOrder.SIZE_ASC -> videos.sortedBy { it.size }
            SortOrder.SIZE_DESC -> videos.sortedByDescending { it.size }
            SortOrder.DURATION_ASC -> videos.sortedBy { it.duration }
            SortOrder.DURATION_DESC -> videos.sortedByDescending { it.duration }
        }
}

enum class SortOrder {
    NAME_ASC, NAME_DESC,
    DATE_ASC, DATE_DESC,
    SIZE_ASC, SIZE_DESC,
    DURATION_ASC, DURATION_DESC
}
