package com.negoplayer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.negoplayer.data.model.MediaFolder
import com.negoplayer.data.model.MediaUiState
import com.negoplayer.data.repository.FileScannerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Folders screen.
 */
class FoldersViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FileScannerRepository(application)

    private val _foldersState = MutableStateFlow<MediaUiState<List<MediaFolder>>>(MediaUiState.Loading)
    val foldersState: StateFlow<MediaUiState<List<MediaFolder>>> = _foldersState.asStateFlow()

    private val _selectedFolder = MutableStateFlow<MediaFolder?>(null)
    val selectedFolder: StateFlow<MediaFolder?> = _selectedFolder.asStateFlow()

    init {
        loadFolders()
    }

    fun loadFolders() {
        viewModelScope.launch {
            _foldersState.value = MediaUiState.Loading
            try {
                val folders = repository.getVideoFolders()
                _foldersState.value = if (folders.isEmpty()) MediaUiState.Empty
                else MediaUiState.Success(folders)
            } catch (e: Exception) {
                _foldersState.value = MediaUiState.Error(e.message ?: "Failed to load folders")
            }
        }
    }

    fun selectFolder(folder: MediaFolder) {
        viewModelScope.launch {
            val items = repository.getItemsInFolder(folder.path)
            _selectedFolder.value = folder.copy(
                items = items,
                videoCount = items.count { it.isVideo },
                audioCount = items.count { it.isAudio }
            )
        }
    }

    fun clearSelection() {
        _selectedFolder.value = null
    }
}
