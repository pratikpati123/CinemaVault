package com.example.cinemavault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cinemavault.presentation.bookmarks.BookmarksScreen
import com.example.cinemavault.presentation.details.DetailScreen
import com.example.cinemavault.presentation.home.HomeScreen
import com.example.cinemavault.presentation.navigation.Screen
import com.example.cinemavault.presentation.search.SearchScreen
import com.example.cinemavault.presentation.theme.CinemaVaultTheme // Ensure your theme exists
import dagger.hilt.android.AndroidEntryPoint

/**
 * The main activity of the application.
 * This activity is the entry point of the application and hosts the main UI, including navigation.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CinemaVaultTheme {
                MainApp()
            }
        }
    }
}

/**
 * The main composable of the application.
 * It sets up the Scaffold, NavHost, and bottom navigation bar.
 */
@Composable
fun MainApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != Screen.Details.route) {
                NavigationBar {
                    // 1. Home Tab
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        selected = currentRoute == Screen.Home.route,
                        onClick = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )

                    // 2. Search Tab
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        label = { Text("Search") },
                        selected = currentRoute == Screen.Search.route,
                        onClick = {
                            navController.navigate(Screen.Search.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )

                    // 3. Saved Tab
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Favorite, contentDescription = "Saved") },
                        label = { Text("Saved") },
                        selected = currentRoute == Screen.Bookmarks.route,
                        onClick = {
                            navController.navigate(Screen.Bookmarks.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
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
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen(navController) }
            composable(Screen.Search.route) { SearchScreen(navController) }

            // 4. Register the Bookmarks Screen
            composable(Screen.Bookmarks.route) { BookmarksScreen(navController) }

            composable(Screen.Details.route) { DetailScreen() }
        }
    }
}
