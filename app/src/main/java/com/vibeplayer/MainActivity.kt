package com.vibeplayer

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.vibeplayer.ui.VibePlayerTheme
import com.vibeplayer.ui.navigation.Screen
import com.vibeplayer.ui.navigation.VibePlayerNavHost
import com.vibeplayer.viewmodel.LibraryViewModel
import com.vibeplayer.viewmodel.PlayerViewModel
import com.vibeplayer.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val playerViewModel: PlayerViewModel by viewModels()
    private val libraryViewModel: LibraryViewModel by viewModels()
    private val searchViewModel: SearchViewModel by viewModels()

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            VibePlayerTheme {
                val permissions = buildList {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        add(Manifest.permission.READ_MEDIA_AUDIO)
                        add(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        add(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
                val permissionsState = rememberMultiplePermissionsState(permissions)

                LaunchedEffect(Unit) {
                    if (!permissionsState.allPermissionsGranted) {
                        permissionsState.launchMultiplePermissionRequest()
                    }
                }

                LaunchedEffect(permissionsState.allPermissionsGranted) {
                    if (permissionsState.allPermissionsGranted) {
                        libraryViewModel.scanLibrary()
                    }
                }

                VibePlayerApp(
                    playerViewModel = playerViewModel,
                    libraryViewModel = libraryViewModel,
                    searchViewModel = searchViewModel
                )
            }
        }
    }
}

@Composable
fun VibePlayerApp(
    playerViewModel: PlayerViewModel,
    libraryViewModel: LibraryViewModel,
    searchViewModel: SearchViewModel
) {
    val navController = rememberNavController()
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem(Screen.Home.route, "Home", Icons.Rounded.Home),
        BottomNavItem(Screen.Library.route, "Library", Icons.Rounded.LibraryMusic),
        BottomNavItem(Screen.Search.route, "Search", Icons.Rounded.Search),
        BottomNavItem(Screen.Settings.route, "Settings", Icons.Rounded.Settings)
    )

    val showBottomNav = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            VibePlayerNavHost(
                navController = navController,
                playerViewModel = playerViewModel,
                libraryViewModel = libraryViewModel,
                searchViewModel = searchViewModel
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
