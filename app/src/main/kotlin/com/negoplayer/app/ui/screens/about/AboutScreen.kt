/*
 * NegoPlayer - Advanced Android Media Platform
 * Author: Muhammad Umar
 * Project: NegoPlayer
 * File: AboutScreen.kt
 */

package com.negoplayer.app.ui.screens.about

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.negoplayer.app.R

/**
 * About Screen - Developer profile, Pakistani pride, and app information.
 * Features: Muhammad Umar Jabbar's photos, Pakistani flag, app details.
 *
 * Author: Muhammad Umar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About NegoPlayer") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // === PAKISTAN PRIDE HEADER ===
            PakistanPrideBanner()

            Spacer(Modifier.height(24.dp))

            // === APP LOGO & INFO ===
            AppInfoSection()

            Spacer(Modifier.height(32.dp))

            // === DEVELOPER SECTION ===
            DeveloperSection()

            Spacer(Modifier.height(32.dp))

            // === FEATURES SECTION ===
            FeaturesSection()

            Spacer(Modifier.height(32.dp))

            // === TECH STACK ===
            TechStackSection()

            Spacer(Modifier.height(32.dp))

            // === FOOTER ===
            FooterSection()

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PakistanPrideBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        // Pakistan Flag Background
        AsyncImage(
            model = R.drawable.pakistan_flag,
            contentDescription = "Pakistan Flag",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF006400).copy(alpha = 0.6f),
                            Color(0xFF0A0A0A)
                        )
                    )
                )
        )

        // Pride Text
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animated flag emoji pulse
            val infiniteTransition = rememberInfiniteTransition(label = "flag")
            val scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "flagScale"
            )

            Text(
                text = "🇵🇰",
                fontSize = 48.sp,
                modifier = Modifier.scale(scale)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Proudly Built in Pakistan",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = "پاکستان زندہ باد",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF69F0AE),
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AppInfoSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        // App Icon placeholder
        Surface(
            modifier = Modifier.size(100.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF1B5E20)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.PlayCircle,
                    contentDescription = "NegoPlayer",
                    tint = Color(0xFF69F0AE),
                    modifier = Modifier.size(60.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "NegoPlayer",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF69F0AE)
        )

        Text(
            text = "Version 1.0.0",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.6f)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "The Ultimate Android Media Platform — Play anything, anywhere, anytime.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.85f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun DeveloperSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Section header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Divider(
                modifier = Modifier.weight(1f),
                color = Color(0xFF00C853).copy(alpha = 0.5f)
            )
            Text(
                text = "  Meet The Developer  ",
                color = Color(0xFF69F0AE),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Divider(
                modifier = Modifier.weight(1f),
                color = Color(0xFF00C853).copy(alpha = 0.5f)
            )
        }

        Spacer(Modifier.height(20.dp))

        // Developer photos - 3 photos in a row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Photo 1
            DeveloperPhoto(
                model = R.drawable.developer_photo1,
                modifier = Modifier.weight(1f)
            )
            // Photo 2 - slightly larger center
            DeveloperPhoto(
                model = R.drawable.developer_photo2,
                modifier = Modifier
                    .weight(1.2f)
                    .offset(y = (-8).dp)
            )
            // Photo 3
            DeveloperPhoto(
                model = R.drawable.developer_photo3,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        // 4th photo - full width feature photo
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Box {
                AsyncImage(
                    model = R.drawable.developer_photo4,
                    contentDescription = "Muhammad Umar Jabbar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.85f)
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Muhammad Umar Jabbar",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Android Developer • Rawalpindi, Pakistan 🇵🇰",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF69F0AE)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Developer bio card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "About the Developer",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF69F0AE)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Muhammad Umar Jabbar is a passionate Android developer from Rawalpindi, Pakistan. " +
                            "With a deep love for clean code and powerful applications, he built NegoPlayer " +
                            "to bring a premium media experience to every Android device. " +
                            "NegoPlayer is his commitment to the open-source community and Pakistani tech innovation.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f),
                    lineHeight = 22.sp
                )

                Spacer(Modifier.height(12.dp))

                // Contact/Social info
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Chip(label = "🇵🇰 Pakistan") {}
                    Chip(label = "📱 Android") {}
                    Chip(label = "💻 Kotlin") {}
                }
            }
        }
    }
}

@Composable
private fun DeveloperPhoto(model: Any, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.aspectRatio(0.75f),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        AsyncImage(
            model = model,
            contentDescription = "Developer Photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun Chip(label: String, onClick: () -> Unit) {
    Surface(
        color = Color(0xFF2D2D2D),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = Color.White
        )
    }
}

@Composable
private fun FeaturesSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Key Features",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF69F0AE),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        val features = listOf(
            "🎬" to "4K & HDR Video Playback",
            "🔊" to "Dolby Audio Compatibility",
            "📺" to "IPTV with M3U Playlist Support",
            "🌐" to "HLS, DASH, RTMP Streaming",
            "📝" to "SRT, ASS, VTT Subtitle Engine",
            "🖼️" to "Picture-in-Picture Mode",
            "⚡" to "Hardware Accelerated Decoding",
            "👆" to "Gesture Controls (Volume/Brightness/Seek)",
            "📚" to "Local Media Library with Metadata",
            "🔖" to "Bookmarks & Watch History",
            "🌙" to "Dark Mode & Material 3 Design",
            "🔒" to "Scoped Storage & DRM Ready"
        )

        features.chunked(2).forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { (emoji, feature) ->
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(emoji, fontSize = 20.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = feature,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun TechStackSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Built With",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF69F0AE),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                TechItem("Kotlin 2.0", "Programming Language")
                TechItem("Jetpack Compose", "Modern UI Framework")
                TechItem("Media3 / ExoPlayer", "Media Playback Engine")
                TechItem("Clean Architecture + MVVM", "Software Architecture")
                TechItem("Hilt", "Dependency Injection")
                TechItem("Room", "Local Database")
                TechItem("Coroutines & Flow", "Async Processing")
                TechItem("Material 3", "Design System")
            }
        }
    }
}

@Composable
private fun TechItem(name: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color(0xFF00C853), CircleShape)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun FooterSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        // Pakistan Flag
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(20.dp))
        ) {
            AsyncImage(
                model = R.drawable.pakistan_flag,
                contentDescription = "Pakistan Flag",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🇵🇰 Proudly Built in Pakistan 🇵🇰",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "From Rawalpindi, to the World",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF69F0AE),
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Italic
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "© 2026 Muhammad Umar Jabbar",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.5f)
        )
        Text(
            text = "NegoPlayer — Open Source Media Platform",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.4f)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Made with ❤️ for Pakistan",
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF69F0AE),
            fontWeight = FontWeight.Bold
        )
    }
}
