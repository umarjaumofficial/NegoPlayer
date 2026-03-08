package com.negoplayer.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.negoplayer.ui.common.BottomNavDestination
import com.negoplayer.ui.folders.FoldersScreen
import com.negoplayer.ui.music.MusicScreen
import com.negoplayer.ui.settings.SettingsScreen
import com.negoplayer.ui.theme.NegoPlayerTheme
import com.negoplayer.ui.videos.VideoPlayerActivity
import com.negoplayer.ui.videos.VideosScreen
import com.negoplayer.utils.Constants
import com.negoplayer.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainViewModel = viewModel()
            NegoPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NegoPlayerApp(
                        onPlayVideo = { uri, title, playlist, index ->
                            val intent = Intent(this, VideoPlayerActivity::class.java).apply {
                                putExtra(Constants.EXTRA_MEDIA_URI, uri)
                                putExtra(Constants.EXTRA_MEDIA_TITLE, title)
                                putExtra(Constants.EXTRA_PLAYLIST_INDEX, index)
                            }
                            startActivity(intent)
                        }
                    )
                }
            }
        }

        // Handle external file open intents
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_VIEW && intent.data != null) {
            val uri = intent.data!!
            val playerIntent = Intent(this, VideoPlayerActivity::class.java).apply {
                putExtra(Constants.EXTRA_MEDIA_URI, uri.toString())
                putExtra(Constants.EXTRA_MEDIA_TITLE, uri.lastPathSegment ?: "")
            }
            startActivity(playerIntent)
        }
    }
}

@Composable
fun NegoPlayerApp(
    onPlayVideo: (uri: String, title: String, playlist: List<String>, index: Int) -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                BottomNavDestination.allDestinations.forEach { destination ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label
                            )
                        },
                        label = { Text(destination.label) },
                        selected = currentDestination?.hierarchy?.any {
                            it.route == destination.route
                        } == true,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavDestination.Videos.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavDestination.Videos.route) {
                VideosScreen(onPlayVideo = onPlayVideo)
            }
            composable(BottomNavDestination.Music.route) {
                MusicScreen()
            }
            composable(BottomNavDestination.Folders.route) {
                FoldersScreen(onPlayVideo = onPlayVideo)
            }
            composable(BottomNavDestination.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
