package com.krithi.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.krithi.ui.components.KrithiBottomNavigation
import com.krithi.ui.components.KrithiTopBar
import com.krithi.ui.components.MiniPlayer
import com.krithi.ui.home.HomeScreen
import com.krithi.ui.library.LibraryScreen
import com.krithi.ui.nowplaying.NowPlayingScreen
import com.krithi.ui.search.SearchScreen
import com.krithi.ui.settings.SettingsScreen
import com.krithi.ui.theme.PrimaryTextDark

@Composable
fun KrithiNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"

    val showBottomNav = currentRoute in listOf("home", "library", "search")
    // In Phase 1, we show MiniPlayer on main screens if wanted, here we just show it as placeholder
    val showMiniPlayer = showBottomNav

    Scaffold(
        topBar = {
            if (showBottomNav) {
                KrithiTopBar(
                    title = currentRoute.replaceFirstChar { it.uppercase() },
                    actions = {
                        IconButton(onClick = { navController.navigate("settings") }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = PrimaryTextDark)
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (showBottomNav) {
                Column {
                    if (showMiniPlayer) {
                        MiniPlayer(onNavigateToNowPlaying = { navController.navigate("now_playing") })
                    }
                    KrithiBottomNavigation(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") { HomeScreen() }
                composable("library") { LibraryScreen() }
                composable("search") { SearchScreen() }
                composable("now_playing") { NowPlayingScreen() }
                composable("settings") { SettingsScreen() }
            }
        }
    }
}
