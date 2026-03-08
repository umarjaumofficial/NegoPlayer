/*
 * NegoPlayer - Advanced Android Media Platform
 * Author: Muhammad Umar
 * File: IptvViewModel.kt
 */

package com.negoplayer.app.ui.screens.iptv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import javax.inject.Inject

data class IptvUiState(
    val channels: List<IptvChannel> = emptyList(),
    val filteredChannels: List<IptvChannel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class IptvViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(IptvUiState())
    val uiState: StateFlow<IptvUiState> = _uiState.asStateFlow()

    fun loadM3uFromUrl(url: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val content = fetchM3u(url)
                val channels = parseM3u(content)
                _uiState.value = _uiState.value.copy(
                    channels = channels,
                    filteredChannels = channels,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load playlist: ${e.message}"
                )
            }
        }
    }

    fun filterChannels(query: String) {
        val filtered = if (query.isBlank()) _uiState.value.channels
        else _uiState.value.channels.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.group.contains(query, ignoreCase = true)
        }
        _uiState.value = _uiState.value.copy(filteredChannels = filtered)
    }

    private suspend fun fetchM3u(url: String): String = withContext(Dispatchers.IO) {
        URL(url).readText()
    }

    /**
     * Parses M3U/M3U8 playlist format.
     * Supports #EXTINF directives with tvg-name, group-title, tvg-logo attributes.
     */
    private fun parseM3u(content: String): List<IptvChannel> {
        val channels = mutableListOf<IptvChannel>()
        if (!content.startsWith("#EXTM3U")) return channels

        val lines = content.lines()
        var i = 0
        var channelNumber = 1

        while (i < lines.size) {
            val line = lines[i].trim()
            if (line.startsWith("#EXTINF:")) {
                val name = extractAttribute(line, "tvg-name")
                    ?: line.substringAfterLast(",").trim()
                val group = extractAttribute(line, "group-title") ?: ""
                val logo = extractAttribute(line, "tvg-logo")

                // Next non-empty, non-comment line is the URL
                var urlLine = ""
                var j = i + 1
                while (j < lines.size) {
                    val next = lines[j].trim()
                    if (next.isNotBlank() && !next.startsWith("#")) {
                        urlLine = next
                        break
                    }
                    j++
                }

                if (urlLine.isNotBlank() && name.isNotBlank()) {
                    channels.add(
                        IptvChannel(
                            name = name,
                            url = urlLine,
                            group = group,
                            logo = logo,
                            number = channelNumber++
                        )
                    )
                }
                i = j + 1
            } else {
                i++
            }
        }

        return channels
    }

    private fun extractAttribute(line: String, attr: String): String? {
        val pattern = Regex("""$attr="([^"]*)"""")
        return pattern.find(line)?.groupValues?.getOrNull(1)?.takeIf { it.isNotBlank() }
    }
}
