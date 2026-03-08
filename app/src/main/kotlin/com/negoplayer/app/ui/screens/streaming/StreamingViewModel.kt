/*
 * NegoPlayer - Advanced Android Media Platform
 * Author: Muhammad Umar
 * File: StreamingViewModel.kt
 */

package com.negoplayer.app.ui.screens.streaming

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class StreamingUiState(
    val history: List<StreamHistoryEntry> = emptyList()
)

@HiltViewModel
class StreamingViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(StreamingUiState())
    val uiState: StateFlow<StreamingUiState> = _uiState.asStateFlow()

    fun addToHistory(url: String, title: String) {
        val entry = StreamHistoryEntry(url = url, title = title)
        val current = _uiState.value.history.toMutableList()
        current.removeAll { it.url == url }
        current.add(0, entry)
        _uiState.value = _uiState.value.copy(history = current.take(20))
    }

    fun removeFromHistory(entry: StreamHistoryEntry) {
        val current = _uiState.value.history.toMutableList()
        current.remove(entry)
        _uiState.value = _uiState.value.copy(history = current)
    }
}
