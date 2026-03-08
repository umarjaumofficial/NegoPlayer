package com.negoplayer.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.negoplayer.viewmodel.SettingsViewModel

/**
 * Settings screen for NegoPlayer.
 */
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val subtitleSize by viewModel.subtitleSize.collectAsState()
    val rememberPosition by viewModel.rememberPosition.collectAsState()
    val gestureEnabled by viewModel.gestureEnabled.collectAsState()
    val doubleTapSeek by viewModel.doubleTapSeek.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Settings") })

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                SettingsSectionHeader("Appearance")
                SettingsToggleItem(
                    icon = Icons.Default.DarkMode,
                    title = "Dark Theme",
                    subtitle = "Use dark colors throughout the app",
                    checked = isDarkTheme,
                    onCheckedChange = { viewModel.setDarkTheme(it) }
                )
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                SettingsSectionHeader("Playback")
                SettingsToggleItem(
                    icon = Icons.Default.History,
                    title = "Remember Playback Position",
                    subtitle = "Resume videos from where you left off",
                    checked = rememberPosition,
                    onCheckedChange = { viewModel.setRememberPosition(it) }
                )
                SettingsToggleItem(
                    icon = Icons.Default.Swipe,
                    title = "Gesture Controls",
                    subtitle = "Swipe to control volume, brightness, and seek",
                    checked = gestureEnabled,
                    onCheckedChange = { viewModel.setGestureEnabled(it) }
                )
                SettingsToggleItem(
                    icon = Icons.Default.TouchApp,
                    title = "Double Tap to Seek",
                    subtitle = "Double tap left/right to seek ±10 seconds",
                    checked = doubleTapSeek,
                    onCheckedChange = { viewModel.setDoubleTapSeek(it) }
                )
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                SettingsSectionHeader("Subtitles")
                SettingsSliderItem(
                    icon = Icons.Default.ClosedCaption,
                    title = "Subtitle Size",
                    subtitle = "Size: ${subtitleSizeLabel(subtitleSize)}",
                    value = subtitleSize.toFloat(),
                    valueRange = 1f..5f,
                    steps = 3,
                    onValueChange = { viewModel.setSubtitleSize(it.toInt()) }
                )
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                SettingsSectionHeader("About")
                SettingsInfoItem(
                    icon = Icons.Default.Info,
                    title = "NegoPlayer",
                    subtitle = "Version 1.0.0 — Built with Media3 & Jetpack Compose"
                )
                SettingsInfoItem(
                    icon = Icons.Default.Person,
                    title = "Developer",
                    subtitle = "Muhammad Umar"
                )
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        leadingContent = {
            Icon(imageVector = icon, contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary)
        },
        trailingContent = {
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    )
}

@Composable
fun SettingsSliderItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(end = 16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge)
                Text(subtitle, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            modifier = Modifier.fillMaxWidth().padding(start = 40.dp)
        )
    }
}

@Composable
fun SettingsInfoItem(icon: ImageVector, title: String, subtitle: String) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        leadingContent = {
            Icon(imageVector = icon, contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary)
        }
    )
}

private fun subtitleSizeLabel(size: Int): String = when (size) {
    1 -> "Small"; 2 -> "Small-Medium"; 3 -> "Medium (Default)"
    4 -> "Large"; 5 -> "Extra Large"; else -> "Medium"
}
