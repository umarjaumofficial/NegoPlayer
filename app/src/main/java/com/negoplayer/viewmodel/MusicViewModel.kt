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
 * ViewModel for the Music screen.
 */
class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MediaRepository(application)

    private val _musicState = MutableStateFlow<MediaUiState<List<MediaItem>>>(MediaUiState.Loading)
    val musicState: StateFlow<MediaUiState<List<MediaItem>>> = _musicState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<MediaItem>>(emptyList())
    val searchResults: StateFlow<List<MediaItem>> = _searchResults.asStateFlow()

    private val _currentTrackIndex = MutableStateFlow(0)
    val currentTrackIndex: StateFlow<Int> = _currentTrackIndex.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _groupBy = MutableStateFlow(MusicGroupBy.ALL)
    val groupBy: StateFlow<MusicGroupBy> = _groupBy.asStateFlow()

    init {
        loadMusic()
    }

    fun loadMusic() {
        viewModelScope.launch {
            _musicState.value = MediaUiState.Loading
            try {
                repository.getAudioFiles().collect { tracks ->
                    _musicState.value = if (tracks.isEmpty()) {
                        MediaUiState.Empty
                    } else {
                        MediaUiState.Success(tracks)
                    }
                }
            } catch (e: Exception) {
                _musicState.value = MediaUiState.Error(e.message ?: "Failed to load music")
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
            _searchResults.value = repository.searchAudio(query)
        }
    }

    fun setGroupBy(groupBy: MusicGroupBy) {
        _groupBy.value = groupBy
    }

    fun setCurrentTrack(index: Int) {
        _currentTrackIndex.value = index
    }

    fun setPlaying(playing: Boolean) {
        _isPlaying.value = playing
    }

    /**
     * Groups a list of tracks by artists.
     */
    fun groupByArtist(tracks: List<MediaItem>): Map<String, List<MediaItem>> =
        tracks.groupBy { it.artist.ifBlank { "Unknown Artist" } }
            .toSortedMap()

    /**
     * Groups a list of tracks by album.
     */
    fun groupByAlbum(tracks: List<MediaItem>): Map<String, List<MediaItem>> =
        tracks.groupBy { it.album.ifBlank { "Unknown Album" } }
            .toSortedMap()
}

enum class MusicGroupBy { ALL, ARTIST, ALBUM }
