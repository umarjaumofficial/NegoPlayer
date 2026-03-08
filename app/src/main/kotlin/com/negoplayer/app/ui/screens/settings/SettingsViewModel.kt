/*
 * NegoPlayer - Advanced Android Media Platform
 * Author: Muhammad Umar
 * File: SettingsViewModel.kt
 */

package com.negoplayer.app.ui.screens.settings

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Context

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nego_settings")

data class NegoPreferences(
    val autoPlayNext: Boolean = true,
    val rememberPosition: Boolean = true,
    val pipEnabled: Boolean = true,
    val autoRotate: Boolean = true,
    val darkMode: Boolean = true,
    val hardwareDecoding: Boolean = true,
    val autoLoadSubtitles: Boolean = true,
    val adaptiveStreaming: Boolean = true,
    val wifiOnly: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val dataStore = application.dataStore

    object Keys {
        val AUTO_PLAY_NEXT = booleanPreferencesKey("auto_play_next")
        val REMEMBER_POSITION = booleanPreferencesKey("remember_position")
        val PIP_ENABLED = booleanPreferencesKey("pip_enabled")
        val AUTO_ROTATE = booleanPreferencesKey("auto_rotate")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val HARDWARE_DECODING = booleanPreferencesKey("hardware_decoding")
        val AUTO_SUBTITLES = booleanPreferencesKey("auto_subtitles")
        val ADAPTIVE_STREAMING = booleanPreferencesKey("adaptive_streaming")
        val WIFI_ONLY = booleanPreferencesKey("wifi_only")
    }

    val preferences = dataStore.data.map { prefs ->
        NegoPreferences(
            autoPlayNext = prefs[Keys.AUTO_PLAY_NEXT] ?: true,
            rememberPosition = prefs[Keys.REMEMBER_POSITION] ?: true,
            pipEnabled = prefs[Keys.PIP_ENABLED] ?: true,
            autoRotate = prefs[Keys.AUTO_ROTATE] ?: true,
            darkMode = prefs[Keys.DARK_MODE] ?: true,
            hardwareDecoding = prefs[Keys.HARDWARE_DECODING] ?: true,
            autoLoadSubtitles = prefs[Keys.AUTO_SUBTITLES] ?: true,
            adaptiveStreaming = prefs[Keys.ADAPTIVE_STREAMING] ?: true,
            wifiOnly = prefs[Keys.WIFI_ONLY] ?: false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NegoPreferences()
    )

    fun setAutoPlayNext(value: Boolean) = save { it[Keys.AUTO_PLAY_NEXT] = value }
    fun setRememberPosition(value: Boolean) = save { it[Keys.REMEMBER_POSITION] = value }
    fun setPipEnabled(value: Boolean) = save { it[Keys.PIP_ENABLED] = value }
    fun setAutoRotate(value: Boolean) = save { it[Keys.AUTO_ROTATE] = value }
    fun setDarkMode(value: Boolean) = save { it[Keys.DARK_MODE] = value }
    fun setHardwareDecoding(value: Boolean) = save { it[Keys.HARDWARE_DECODING] = value }
    fun setAutoLoadSubtitles(value: Boolean) = save { it[Keys.AUTO_SUBTITLES] = value }
    fun setAdaptiveStreaming(value: Boolean) = save { it[Keys.ADAPTIVE_STREAMING] = value }
    fun setWifiOnly(value: Boolean) = save { it[Keys.WIFI_ONLY] = value }

    private fun save(block: suspend (MutablePreferences) -> Unit) {
        viewModelScope.launch { dataStore.edit { block(it) } }
    }
}
