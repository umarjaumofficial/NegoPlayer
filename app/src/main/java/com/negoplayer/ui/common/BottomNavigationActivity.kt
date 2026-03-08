package com.negoplayer.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Defines the bottom navigation destinations for NegoPlayer.
 */
sealed class BottomNavDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Videos : BottomNavDestination(
        route = "videos",
        label = "Videos",
        icon = Icons.Default.VideoLibrary
    )

    object Music : BottomNavDestination(
        route = "music",
        label = "Music",
        icon = Icons.Default.MusicNote
    )

    object Folders : BottomNavDestination(
        route = "folders",
        label = "Folders",
        icon = Icons.Default.Folder
    )

    object Settings : BottomNavDestination(
        route = "settings",
        label = "Settings",
        icon = Icons.Default.Settings
    )

    companion object {
        val allDestinations = listOf(Videos, Music, Folders, Settings)
    }
}
