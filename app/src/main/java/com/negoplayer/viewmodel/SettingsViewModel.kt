package com.negoplayer.viewmodel

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.negoplayer.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore(name = Constants.PREFS_NAME)

/**
 * ViewModel for the Settings screen.
 * Persists preferences using DataStore.
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStore = application.dataStore

    // Keys
    private val KEY_DARK_THEME = booleanPreferencesKey(Constants.PREF_THEME)
    private val KEY_SUBTITLE_SIZE = intPreferencesKey(Constants.PREF_SUBTITLE_SIZE)
    private val KEY_REMEMBER_POSITION = booleanPreferencesKey(Constants.PREF_REMEMBER_POSITION)
    private val KEY_GESTURE_ENABLED = booleanPreferencesKey(Constants.PREF_GESTURE_ENABLED)
    private val KEY_DOUBLE_TAP_SEEK = booleanPreferencesKey(Constants.PREF_DOUBLE_TAP_SEEK)

    // State
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _subtitleSize = MutableStateFlow(3)
    val subtitleSize: StateFlow<Int> = _subtitleSize.asStateFlow()

    private val _rememberPosition = MutableStateFlow(true)
    val rememberPosition: StateFlow<Boolean> = _rememberPosition.asStateFlow()

    private val _gestureEnabled = MutableStateFlow(true)
    val gestureEnabled: StateFlow<Boolean> = _gestureEnabled.asStateFlow()

    private val _doubleTapSeek = MutableStateFlow(true)
    val doubleTapSeek: StateFlow<Boolean> = _doubleTapSeek.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            try {
                val prefs = dataStore.data.first()
                _isDarkTheme.value = prefs[KEY_DARK_THEME] ?: false
                _subtitleSize.value = prefs[KEY_SUBTITLE_SIZE] ?: 3
                _rememberPosition.value = prefs[KEY_REMEMBER_POSITION] ?: true
                _gestureEnabled.value = prefs[KEY_GESTURE_ENABLED] ?: true
                _doubleTapSeek.value = prefs[KEY_DOUBLE_TAP_SEEK] ?: true
            } catch (_: Exception) {}
        }
    }

    fun setDarkTheme(enabled: Boolean) {
        _isDarkTheme.value = enabled
        viewModelScope.launch {
            dataStore.edit { it[KEY_DARK_THEME] = enabled }
        }
    }

    fun setSubtitleSize(size: Int) {
        _subtitleSize.value = size
        viewModelScope.launch {
            dataStore.edit { it[KEY_SUBTITLE_SIZE] = size }
        }
    }

    fun setRememberPosition(enabled: Boolean) {
        _rememberPosition.value = enabled
        viewModelScope.launch {
            dataStore.edit { it[KEY_REMEMBER_POSITION] = enabled }
        }
    }

    fun setGestureEnabled(enabled: Boolean) {
        _gestureEnabled.value = enabled
        viewModelScope.launch {
            dataStore.edit { it[KEY_GESTURE_ENABLED] = enabled }
        }
    }

    fun setDoubleTapSeek(enabled: Boolean) {
        _doubleTapSeek.value = enabled
        viewModelScope.launch {
            dataStore.edit { it[KEY_DOUBLE_TAP_SEEK] = enabled }
        }
    }
}
