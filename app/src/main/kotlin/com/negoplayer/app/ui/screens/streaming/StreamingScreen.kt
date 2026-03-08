/*
 * NegoPlayer - Advanced Android Media Platform
 * Author: Muhammad Umar
 * Project: NegoPlayer
 * File: StreamingScreen.kt
 */

package com.negoplayer.app.ui.screens.streaming

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Streaming Screen - Play online content via URL, HLS, DASH, RTMP.
 *
 * Author: Muhammad Umar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreamingScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPlayer: (uri: String, title: String) -> Unit,
    viewModel: StreamingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var streamUrl by remember { mutableStateOf("") }
    var streamTitle by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Streaming") },
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
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // URL input card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Link, tint = Color(0xFF69F0AE), contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Open Network Stream",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = streamUrl,
                            onValueChange = { streamUrl = it },
                            label = { Text("Stream URL") },
                            placeholder = { Text("https://, rtmp://, rtsp://...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF00C853),
                                focusedLabelColor = Color(0xFF00C853),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                containerColor = Color(0xFF0A0A0A)
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = streamTitle,
                            onValueChange = { streamTitle = it },
                            label = { Text("Title (optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF00C853),
                                focusedLabelColor = Color(0xFF00C853),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                containerColor = Color(0xFF0A0A0A)
                            )
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = {
                                if (streamUrl.isNotBlank()) {
                                    val title = streamTitle.ifBlank { "Network Stream" }
                                    viewModel.addToHistory(streamUrl, title)
                                    onNavigateToPlayer(streamUrl.trim(), title)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text("Play Stream", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Supported formats
            item {
                SupportedFormatsCard()
            }

            // History
            if (uiState.history.isNotEmpty()) {
                item {
                    Text(
                        "Recent Streams",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                items(uiState.history) { item ->
                    StreamHistoryItem(
                        item = item,
                        onClick = { onNavigateToPlayer(item.url, item.title) },
                        onDelete = { viewModel.removeFromHistory(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SupportedFormatsCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Supported Protocols",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF69F0AE)
            )
            Spacer(Modifier.height(8.dp))
            val protocols = listOf(
                "HTTP/HTTPS" to "Direct URL streaming",
                "HLS (m3u8)" to "Adaptive bitrate streaming",
                "DASH (mpd)" to "Dynamic adaptive streaming",
                "RTMP" to "Real-Time Messaging Protocol",
                "RTSP" to "Real Time Streaming Protocol"
            )
            protocols.forEach { (proto, desc) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        proto,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        desc,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StreamHistoryItem(
    item: StreamHistoryEntry,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.History,
                contentDescription = null,
                tint = Color(0xFF69F0AE),
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Text(
                    item.url,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.4f),
                    maxLines = 1
                )
            }
            IconButton(onClick = onClick) {
                Icon(Icons.Default.PlayArrow, tint = Color(0xFF00C853), contentDescription = "Play")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, tint = Color.Red.copy(alpha = 0.7f), contentDescription = "Delete")
            }
        }
    }
}

data class StreamHistoryEntry(
    val url: String,
    val title: String,
    val timestamp: Long = System.currentTimeMillis()
)
