/*
 * NegoPlayer - Advanced Android Media Platform
 * Author: Muhammad Umar
 * Project: NegoPlayer
 * File: Navigation.kt
 */

package com.negoplayer.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.negoplayer.app.ui.screens.about.AboutScreen
import com.negoplayer.app.ui.screens.home.HomeScreen
import com.negoplayer.app.ui.screens.iptv.IptvScreen
import com.negoplayer.app.ui.screens.library.LibraryScreen
import com.negoplayer.app.ui.screens.player.PlayerScreen
import com.negoplayer.app.ui.screens.settings.SettingsScreen
import com.negoplayer.app.ui.screens.streaming.StreamingScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Screen route definitions for NegoPlayer navigation.
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Library : Screen("library")
    object Player : Screen("player/{mediaUri}/{mediaTitle}") {
        fun createRoute(uri: String, title: String): String {
            val encoded = URLEncoder.encode(uri, StandardCharsets.UTF_8.toString())
            val encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.toString())
            return "player/$encoded/$encodedTitle"
        }
    }
    object Streaming : Screen("streaming")
    object Iptv : Screen("iptv")
    object Settings : Screen("settings")
    object About : Screen("about")
}

/**
 * Main navigation host for NegoPlayer.
 */
@Composable
fun NegoNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        }
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToLibrary = { navController.navigate(Screen.Library.route) },
                onNavigateToPlayer = { uri, title ->
                    navController.navigate(Screen.Player.createRoute(uri, title))
                },
                onNavigateToStreaming = { navController.navigate(Screen.Streaming.route) },
                onNavigateToIptv = { navController.navigate(Screen.Iptv.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToAbout = { navController.navigate(Screen.About.route) }
            )
        }

        composable(Screen.Library.route) {
            LibraryScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPlayer = { uri, title ->
                    navController.navigate(Screen.Player.createRoute(uri, title))
                }
            )
        }

        composable(
            route = Screen.Player.route,
            arguments = listOf(
                navArgument("mediaUri") { type = NavType.StringType },
                navArgument("mediaTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val encodedUri = backStackEntry.arguments?.getString("mediaUri") ?: ""
            val encodedTitle = backStackEntry.arguments?.getString("mediaTitle") ?: ""
            val mediaUri = URLDecoder.decode(encodedUri, StandardCharsets.UTF_8.toString())
            val mediaTitle = URLDecoder.decode(encodedTitle, StandardCharsets.UTF_8.toString())
            PlayerScreen(
                mediaUri = mediaUri,
                mediaTitle = mediaTitle,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Streaming.route) {
            StreamingScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPlayer = { uri, title ->
                    navController.navigate(Screen.Player.createRoute(uri, title))
                }
            )
        }

        composable(Screen.Iptv.route) {
            IptvScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPlayer = { uri, title ->
                    navController.navigate(Screen.Player.createRoute(uri, title))
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.About.route) {
            AboutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
