/*
 * NegoPlayer - Advanced Android Media Platform
 * Author: Muhammad Umar
 * File: Components.kt
 */

package com.negoplayer.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Shared UI components for NegoPlayer.
 *
 * Author: Muhammad Umar
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NegoTopBar(
    title: String,
    onSettingsClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    TopAppBar(
        title = { Text(title, color = Color(0xFF69F0AE)) },
        actions = {
            IconButton(onClick = onAboutClick) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = "About",
                    tint = Color.White
                )
            }
            IconButton(onClick = onSettingsClick) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF0A0A0A),
            titleContentColor = Color(0xFF69F0AE)
        )
    )
}

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigateToHome: () -> Unit,
    onNavigateToLibrary: () -> Unit,
    onNavigateToStreaming: () -> Unit,
    onNavigateToIptv: () -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF0A0A0A),
        contentColor = Color(0xFF69F0AE)
    ) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = onNavigateToHome,
            icon = { Icon(Icons.Default.Home, "Home") },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF00C853),
                selectedTextColor = Color(0xFF00C853),
                unselectedIconColor = Color.White.copy(alpha = 0.5f),
                unselectedTextColor = Color.White.copy(alpha = 0.5f),
                indicatorColor = Color(0xFF1B5E20)
            )
        )
        NavigationBarItem(
            selected = currentRoute == "library",
            onClick = onNavigateToLibrary,
            icon = { Icon(Icons.Default.VideoLibrary, "Library") },
            label = { Text("Library") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF00C853),
                selectedTextColor = Color(0xFF00C853),
                unselectedIconColor = Color.White.copy(alpha = 0.5f),
                unselectedTextColor = Color.White.copy(alpha = 0.5f),
                indicatorColor = Color(0xFF1B5E20)
            )
        )
        NavigationBarItem(
            selected = currentRoute == "streaming",
            onClick = onNavigateToStreaming,
            icon = { Icon(Icons.Default.Cast, "Streaming") },
            label = { Text("Stream") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF00C853),
                selectedTextColor = Color(0xFF00C853),
                unselectedIconColor = Color.White.copy(alpha = 0.5f),
                unselectedTextColor = Color.White.copy(alpha = 0.5f),
                indicatorColor = Color(0xFF1B5E20)
            )
        )
        NavigationBarItem(
            selected = currentRoute == "iptv",
            onClick = onNavigateToIptv,
            icon = { Icon(Icons.Default.LiveTv, "IPTV") },
            label = { Text("IPTV") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF00C853),
                selectedTextColor = Color(0xFF00C853),
                unselectedIconColor = Color.White.copy(alpha = 0.5f),
                unselectedTextColor = Color.White.copy(alpha = 0.5f),
                indicatorColor = Color(0xFF1B5E20)
            )
        )
    }
}
