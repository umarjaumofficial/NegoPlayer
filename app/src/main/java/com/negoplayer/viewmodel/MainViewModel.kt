package com.negoplayer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.negoplayer.data.model.PlaybackInfo
import com.negoplayer.data.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for the main activity. Manages global app state
 * including the currently playing item (mini-player).
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    val mediaRepository = MediaRepository(application)

    private val _currentPlaybackInfo = MutableStateFlow(PlaybackInfo())
    val currentPlaybackInfo: StateFlow<PlaybackInfo> = _currentPlaybackInfo.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun updatePlaybackInfo(info: PlaybackInfo) {
        _currentPlaybackInfo.value = info
    }

    fun clearPlayback() {
        _currentPlaybackInfo.value = PlaybackInfo()
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }
}
