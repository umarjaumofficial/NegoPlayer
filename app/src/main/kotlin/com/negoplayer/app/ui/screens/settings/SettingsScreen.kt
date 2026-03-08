/*
 * NegoPlayer - Advanced Android Media Platform
 * Author: Muhammad Umar
 * File: SettingsScreen.kt
 */

package com.negoplayer.app.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Settings Screen - User preferences and configuration.
 *
 * Author: Muhammad Umar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val prefs by viewModel.preferences.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0A0A0A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF0A0A0A)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { SettingsSectionHeader("Playback") }

            item {
                SwitchSettingItem(
                    icon = Icons.Default.PlayCircle,
                    title = "Auto Play Next",
                    subtitle = "Automatically play next video in queue",
                    checked = prefs.autoPlayNext,
                    onCheckedChange = { viewModel.setAutoPlayNext(it) }
                )
            }

            item {
                SwitchSettingItem(
                    icon = Icons.Default.History,
                    title = "Remember Position",
                    subtitle = "Resume from where you left off",
                    checked = prefs.rememberPosition,
                    onCheckedChange = { viewModel.setRememberPosition(it) }
                )
            }

            item {
                SwitchSettingItem(
                    icon = Icons.Default.PictureInPicture,
                    title = "Picture-in-Picture",
                    subtitle = "Enable PiP when switching apps",
                    checked = prefs.pipEnabled,
                    onCheckedChange = { viewModel.setPipEnabled(it) }
                )
            }

            item {
                SwitchSettingItem(
                    icon = Icons.Default.ScreenRotation,
                    title = "Auto Rotate",
                    subtitle = "Rotate screen with video orientation",
                    checked = prefs.autoRotate,
                    onCheckedChange = { viewModel.setAutoRotate(it) }
                )
            }

            item { SettingsSectionHeader("Display") }

            item {
                SwitchSettingItem(
                    icon = Icons.Default.DarkMode,
                    title = "Dark Mode",
                    subtitle = "Use dark theme (default)",
                    checked = prefs.darkMode,
                    onCheckedChange = { viewModel.setDarkMode(it) }
                )
            }

            item {
                SwitchSettingItem(
                    icon = Icons.Default.Hd,
                    title = "Hardware Decoding",
                    subtitle = "Use GPU for video decoding",
                    checked = prefs.hardwareDecoding,
                    onCheckedChange = { viewModel.setHardwareDecoding(it) }
                )
            }

            item { SettingsSectionHeader("Subtitles") }

            item {
                SwitchSettingItem(
                    icon = Icons.Default.Subtitles,
                    title = "Auto Load Subtitles",
                    subtitle = "Load subtitle files automatically",
                    checked = prefs.autoLoadSubtitles,
                    onCheckedChange = { viewModel.setAutoLoadSubtitles(it) }
                )
            }

            item { SettingsSectionHeader("Network") }

            item {
                SwitchSettingItem(
                    icon = Icons.Default.NetworkCheck,
                    title = "Adaptive Streaming",
                    subtitle = "Adjust quality based on connection",
                    checked = prefs.adaptiveStreaming,
                    onCheckedChange = { viewModel.setAdaptiveStreaming(it) }
                )
            }

            item {
                SwitchSettingItem(
                    icon = Icons.Default.Wifi,
                    title = "WiFi Only Streaming",
                    subtitle = "Stream only on WiFi to save data",
                    checked = prefs.wifiOnly,
                    onCheckedChange = { viewModel.setWifiOnly(it) }
                )
            }

            item { SettingsSectionHeader("About") }

            item {
                InfoSettingItem(
                    icon = Icons.Default.Info,
                    title = "Version",
                    subtitle = "1.0.0"
                )
            }

            item {
                InfoSettingItem(
                    icon = Icons.Default.Person,
                    title = "Developer",
                    subtitle = "Muhammad Umar Jabbar"
                )
            }

            item {
                InfoSettingItem(
                    icon = Icons.Default.Flag,
                    title = "Made in",
                    subtitle = "🇵🇰 Pakistan"
                )
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF69F0AE),
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun SwitchSettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF69F0AE), modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = Color.White)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.5f))
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = Color(0xFF00C853)
                )
            )
        }
    }
}

@Composable
private fun InfoSettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF69F0AE), modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(16.dp))
            Text(title, style = MaterialTheme.typography.bodyMedium, color = Color.White, modifier = Modifier.weight(1f))
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Medium)
        }
    }
}
