package com.vibeplayer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vibeplayer.ui.screens.home.HomeScreen
import com.vibeplayer.ui.screens.library.LibraryScreen
import com.vibeplayer.ui.screens.player.FullPlayerScreen
import com.vibeplayer.ui.screens.search.SearchScreen
import com.vibeplayer.ui.screens.settings.SettingsScreen
import com.vibeplayer.viewmodel.LibraryViewModel
import com.vibeplayer.viewmodel.PlayerViewModel
import com.vibeplayer.viewmodel.SearchViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Library : Screen("library")
    object Search : Screen("search")
    object Settings : Screen("settings")
    object FullPlayer : Screen("full_player")
    object AlbumDetail : Screen("album/{albumId}") {
        fun createRoute(albumId: Long) = "album/$albumId"
    }
    object ArtistDetail : Screen("artist/{artistId}") {
        fun createRoute(artistId: Long) = "artist/$artistId"
    }
    object PlaylistDetail : Screen("playlist/{playlistId}") {
        fun createRoute(playlistId: Long) = "playlist/$playlistId"
    }
}

@Composable
fun VibePlayerNavHost(
    navController: NavHostController,
    playerViewModel: PlayerViewModel,
    libraryViewModel: LibraryViewModel,
    searchViewModel: SearchViewModel
) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                playerViewModel = playerViewModel,
                libraryViewModel = libraryViewModel,
                onNavigateToPlayer = { navController.navigate(Screen.FullPlayer.route) },
                onNavigateToSearch = { navController.navigate(Screen.Search.route) }
            )
        }
        composable(Screen.Library.route) {
            LibraryScreen(
                playerViewModel = playerViewModel,
                libraryViewModel = libraryViewModel,
                onNavigateToPlayer = { navController.navigate(Screen.FullPlayer.route) },
                onNavigateToAlbum = { id -> navController.navigate(Screen.AlbumDetail.createRoute(id)) },
                onNavigateToArtist = { id -> navController.navigate(Screen.ArtistDetail.createRoute(id)) },
                onNavigateToPlaylist = { id -> navController.navigate(Screen.PlaylistDetail.createRoute(id)) }
            )
        }
        composable(Screen.Search.route) {
            SearchScreen(
                playerViewModel = playerViewModel,
                searchViewModel = searchViewModel,
                onNavigateToPlayer = { navController.navigate(Screen.FullPlayer.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.FullPlayer.route) {
            FullPlayerScreen(
                playerViewModel = playerViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
