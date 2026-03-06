package com.vibeplayer.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vibeplayer.ui.components.*
import com.vibeplayer.viewmodel.LibraryViewModel
import com.vibeplayer.viewmodel.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    playerViewModel: PlayerViewModel,
    libraryViewModel: LibraryViewModel,
    onNavigateToPlayer: () -> Unit,
    onNavigateToSearch: () -> Unit
) {
    val playerState by playerViewModel.playerState.collectAsState()
    val libraryState by libraryViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("VibePlayer", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Your Music", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(Icons.Rounded.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Rounded.AccountCircle, contentDescription = "Profile")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            if (playerState.currentSong != null) {
                MiniPlayer(
                    playerState = playerState,
                    onTap = onNavigateToPlayer,
                    onPlayPause = playerViewModel::playPause,
                    onSkipNext = playerViewModel::skipNext
                )
            }
        }
    ) { padding ->
        if (libraryState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Quick Actions
            item {
                QuickActionsRow(
                    onShuffleAll = {
                        if (libraryState.songs.isNotEmpty()) {
                            val shuffled = libraryState.songs.shuffled()
                            playerViewModel.playSong(shuffled.first(), shuffled, 0)
                        }
                    },
                    onFavorites = {
                        if (libraryState.favoriteSongs.isNotEmpty()) {
                            playerViewModel.playSong(
                                libraryState.favoriteSongs.first(),
                                libraryState.favoriteSongs, 0
                            )
                        }
                    }
                )
            }

            // Recently Played
            if (libraryState.recentlyPlayed.isNotEmpty()) {
                item { SectionHeader("Recently Played") }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(libraryState.recentlyPlayed.take(10)) { song ->
                            RecentSongCard(
                                song = song,
                                isPlaying = playerState.currentSong?.id == song.id,
                                onClick = {
                                    playerViewModel.playSong(song, libraryState.recentlyPlayed, libraryState.recentlyPlayed.indexOf(song))
                                    onNavigateToPlayer()
                                }
                            )
                        }
                    }
                }
            }

            // Recently Added
            if (libraryState.recentlyAdded.isNotEmpty()) {
                item { SectionHeader("Recently Added") }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(libraryState.recentlyAdded.take(10)) { song ->
                            RecentSongCard(
                                song = song,
                                isPlaying = playerState.currentSong?.id == song.id,
                                onClick = {
                                    playerViewModel.playSong(song, libraryState.recentlyAdded, libraryState.recentlyAdded.indexOf(song))
                                    onNavigateToPlayer()
                                }
                            )
                        }
                    }
                }
            }

            // Most Played
            if (libraryState.mostPlayed.isNotEmpty()) {
                item { SectionHeader("Most Played") }
                items(libraryState.mostPlayed.take(5)) { song ->
                    SongItem(
                        song = song,
                        isPlaying = playerState.currentSong?.id == song.id && playerState.isPlaying,
                        onClick = {
                            playerViewModel.playSong(song, libraryState.mostPlayed, libraryState.mostPlayed.indexOf(song))
                            onNavigateToPlayer()
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                    )
                }
            }

            // Albums
            if (libraryState.albums.isNotEmpty()) {
                item { SectionHeader("Albums") }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(libraryState.albums.take(15)) { album ->
                            AlbumCard(album = album, onClick = {})
                        }
                    }
                }
            }

            // Scanning indicator
            if (libraryState.isScanning) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                        Text("Scanning library...", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionsRow(onShuffleAll: () -> Unit, onFavorites: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FilledTonalButton(
            onClick = onShuffleAll,
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Rounded.Shuffle, null, Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Shuffle All")
        }
        FilledTonalButton(
            onClick = onFavorites,
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Rounded.Favorite, null, Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Favorites")
        }
    }
}

@Composable
fun RecentSongCard(
    song: com.vibeplayer.data.model.Song,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        color = if (isPlaying)
            MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.width(160.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            AlbumArt(
                uri = song.albumArtUri,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
            )
            Spacer(Modifier.height(8.dp))
            androidx.compose.material3.Text(
                song.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            androidx.compose.material3.Text(
                song.artist,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}
