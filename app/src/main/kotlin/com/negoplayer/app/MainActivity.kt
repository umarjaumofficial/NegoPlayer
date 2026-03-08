/*
 * NegoPlayer - Advanced Android Media Platform
 * Author: Muhammad Umar
 * Project: NegoPlayer
 * File: MainActivity.kt
 */

package com.negoplayer.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.negoplayer.app.navigation.NegoNavHost
import com.negoplayer.app.ui.theme.NegoPlayerTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * NegoPlayer Main Activity.
 * Single-activity architecture with Jetpack Compose Navigation.
 *
 * Author: Muhammad Umar
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NegoPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NegoPlayerApp()
                }
            }
        }
    }
}

@Composable
fun NegoPlayerApp() {
    NegoNavHost()
}
