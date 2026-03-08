/*
 * NegoPlayer - Advanced Android Media Platform
 * Author: Muhammad Umar
 * Project: NegoPlayer
 * File: HomeScreen.kt
 */

package com.negoplayer.app.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.negoplayer.app.ui.components.BottomNavigationBar
import com.negoplayer.app.ui.components.NegoTopBar

/**
 * Home Screen - NegoPlayer main dashboard.
 * Shows recent media, quick actions, and navigation to all features.
 *
 * Author: Muhammad Umar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToLibrary: () -> Unit,
    onNavigateToPlayer: (uri: String, title: String) -> Unit,
    onNavigateToStreaming: () -> Unit,
    onNavigateToIptv: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            NegoTopBar(
                title = "NegoPlayer",
                onSettingsClick = onNavigateToSettings,
                onAboutClick = onNavigateToAbout
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "home",
                onNavigateToHome = {},
                onNavigateToLibrary = onNavigateToLibrary,
                onNavigateToStreaming = onNavigateToStreaming,
                onNavigateToIptv = onNavigateToIptv
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Banner
            item {
                HeroBanner(
                    onStreamingClick = onNavigateToStreaming,
                    onNetworkStreamClick = onNavigateToStreaming
                )
            }

            // Quick Feature Cards
            item {
                QuickFeaturesSection(
                    onLibraryClick = onNavigateToLibrary,
                    onStreamingClick = onNavigateToStreaming,
                    onIptvClick = onNavigateToIptv
                )
            }

            // Recent Played
            if (uiState.recentMedia.isNotEmpty()) {
                item {
                    SectionTitle(title = "Continue Watching")
                }
                item {
                    RecentMediaRow(
                        items = uiState.recentMedia,
                        onItemClick = { item ->
                            onNavigateToPlayer(item.uri, item.title)
                        }
                    )
                }
            }

            // Network Stream Input
            item {
                NetworkStreamCard(
                    onPlay = { url ->
                        onNavigateToPlayer(url, "Network Stream")
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun HeroBanner(
    onStreamingClick: () -> Unit,
    onNetworkStreamClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1B5E20),
                        Color(0xFF0A0A0A)
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Column {
            Text(
                text = "NegoPlayer",
                style = MaterialTheme.typography.displaySmall,
                color = Color(0xFF69F0AE),
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Ultimate Media Experience",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onStreamingClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00C853)
                    )
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Stream Now")
                }
                OutlinedButton(
                    onClick = onNetworkStreamClick,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF69F0AE)
                    )
                ) {
                    Text("Network URL")
                }
            }
        }
    }
}

@Composable
private fun QuickFeaturesSection(
    onLibraryClick: () -> Unit,
    onStreamingClick: () -> Unit,
    onIptvClick: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Features",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FeatureCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.VideoLibrary,
                title = "Library",
                subtitle = "Local Videos",
                color = Color(0xFF1565C0),
                onClick = onLibraryClick
            )
            FeatureCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Cast,
                title = "Streaming",
                subtitle = "Online Media",
                color = Color(0xFF6A1B9A),
                onClick = onStreamingClick
            )
            FeatureCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.LiveTv,
                title = "IPTV",
                subtitle = "Live TV",
                color = Color(0xFFBF360C),
                onClick = onIptvClick
            )
        }
    }
}

@Composable
private fun FeatureCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = tween(100),
        label = "scale"
    )

    Card(
        modifier = modifier
            .scale(scale)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
private fun RecentMediaRow(
    items: List<RecentMediaItem>,
    onItemClick: (RecentMediaItem) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { item ->
            RecentMediaCard(item = item, onClick = { onItemClick(item) })
        }
    }
}

@Composable
private fun RecentMediaCard(
    item: RecentMediaItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
            ) {
                AsyncImage(
                    model = item.thumbnailUri,
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Progress overlay
                if (item.progress > 0f) {
                    LinearProgressIndicator(
                        progress = { item.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .height(3.dp),
                        color = Color(0xFF00C853),
                        trackColor = Color.Transparent
                    )
                }
                // Play button overlay
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = "Play",
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.Center)
                )
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.duration,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NetworkStreamCard(onPlay: (String) -> Unit) {
    var url by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Link,
                    contentDescription = null,
                    tint = Color(0xFF69F0AE)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Open Network Stream",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("Enter URL (HTTP, HLS, DASH, RTMP)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF00C853),
                    focusedLabelColor = Color(0xFF00C853)
                )
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { if (url.isNotBlank()) onPlay(url.trim()) },
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

data class RecentMediaItem(
    val id: Long,
    val uri: String,
    val title: String,
    val duration: String,
    val thumbnailUri: String?,
    val progress: Float = 0f
)
