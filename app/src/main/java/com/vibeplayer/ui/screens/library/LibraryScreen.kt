package com.vibeplayer.ui.screens.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vibeplayer.data.model.Playlist
import com.vibeplayer.data.model.Song
import com.vibeplayer.ui.components.*
import com.vibeplayer.viewmodel.LibraryViewModel
import com.vibeplayer.viewmodel.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    playerViewModel: PlayerViewModel,
    libraryViewModel: LibraryViewModel,
    onNavigateToPlayer: () -> Unit,
    onNavigateToAlbum: (Long) -> Unit,
    onNavigateToArtist: (Long) -> Unit,
    onNavigateToPlaylist: (Long) -> Unit
) {
    val playerState by playerViewModel.playerState.collectAsState()
    val libraryState by libraryViewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Songs", "Albums", "Artists", "Playlists")
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Library", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                actions = {
                    if (libraryState.isScanning) {
                        CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                    }
                    IconButton(onClick = libraryViewModel::scanLibrary) {
                        Icon(Icons.Rounded.Refresh, contentDescription = "Refresh")
                    }
                    if (selectedTab == 3) {
                        IconButton(onClick = { showCreatePlaylistDialog = true }) {
                            Icon(Icons.Rounded.Add, contentDescription = "New Playlist")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
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
        Column(modifier = Modifier.padding(padding)) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.background,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(title) })
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            when (selectedTab) {
                0 -> SongsTab(
                    songs = libraryState.songs,
                    currentSong = playerState.currentSong,
                    isPlaying = playerState.isPlaying,
                    onSongClick = { song ->
                        playerViewModel.playSong(song, libraryState.songs, libraryState.songs.indexOf(song))
                        onNavigateToPlayer()
                    }
                )
                1 -> AlbumsTab(albums = libraryState.albums, onAlbumClick = onNavigateToAlbum)
                2 -> ArtistsTab(artists = libraryState.artists, onArtistClick = onNavigateToArtist)
                3 -> PlaylistsTab(
                    playlists = libraryState.playlists,
                    favoriteSongs = libraryState.favoriteSongs,
                    onPlaylistClick = onNavigateToPlaylist,
                    onDeletePlaylist = { libraryViewModel.deletePlaylist(it) }
                )
            }
        }
    }

    if (showCreatePlaylistDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreatePlaylistDialog = false },
            onCreate = { name -> libraryViewModel.createPlaylist(name); showCreatePlaylistDialog = false }
        )
    }
}

@Composable
private fun SongsTab(songs: List<Song>, currentSong: Song?, isPlaying: Boolean, onSongClick: (Song) -> Unit) {
    if (songs.isEmpty()) { EmptyState("No songs found", "Your music library is empty", Icons.Rounded.MusicNote); return }
    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
        items(songs, key = { it.id }) { song ->
            SongItem(
                song = song,
                isPlaying = currentSong?.id == song.id && isPlaying,
                onClick = { onSongClick(song) },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
private fun AlbumsTab(albums: List<com.vibeplayer.data.model.Album>, onAlbumClick: (Long) -> Unit) {
    if (albums.isEmpty()) { EmptyState("No albums found", "Albums will appear here", Icons.Rounded.Album); return }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(albums.size) { i ->
            AlbumCard(album = albums[i], onClick = { onAlbumClick(albums[i].id) })
        }
    }
}

@Composable
private fun ArtistsTab(artists: List<com.vibeplayer.data.model.Artist>, onArtistClick: (Long) -> Unit) {
    if (artists.isEmpty()) { EmptyState("No artists found", "Artists will appear here", Icons.Rounded.Person); return }
    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
        items(artists, key = { it.id }) { artist ->
            ListItem(
                headlineContent = { Text(artist.name, fontWeight = FontWeight.Medium) },
                supportingContent = { Text("${artist.albumCount} albums • ${artist.songCount} songs", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                leadingContent = {
                    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(48.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(artist.name.firstOrNull()?.uppercaseChar()?.toString() ?: "?", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                },
                modifier = Modifier.clickable { onArtistClick(artist.id) }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

@Composable
private fun PlaylistsTab(playlists: List<Playlist>, favoriteSongs: List<Song>, onPlaylistClick: (Long) -> Unit, onDeletePlaylist: (Long) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
        item {
            ListItem(
                headlineContent = { Text("Favorites", fontWeight = FontWeight.Medium) },
                supportingContent = { Text("${favoriteSongs.size} songs") },
                leadingContent = {
                    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.errorContainer, modifier = Modifier.size(48.dp)) {
                        Box(contentAlignment = Alignment.Center) { Icon(Icons.Rounded.Favorite, null, tint = MaterialTheme.colorScheme.error) }
                    }
                }
            )
        }
        items(playlists, key = { it.id }) { playlist ->
            var showMenu by remember { mutableStateOf(false) }
            ListItem(
                headlineContent = { Text(playlist.name, fontWeight = FontWeight.Medium) },
                leadingContent = {
                    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier.size(48.dp)) {
                        Box(contentAlignment = Alignment.Center) { Icon(Icons.Rounded.QueueMusic, null, tint = MaterialTheme.colorScheme.secondary) }
                    }
                },
                trailingContent = {
                    Box {
                        IconButton(onClick = { showMenu = true }) { Icon(Icons.Rounded.MoreVert, null) }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(text = { Text("Delete") }, onClick = { onDeletePlaylist(playlist.id); showMenu = false }, leadingIcon = { Icon(Icons.Rounded.Delete, null) })
                        }
                    }
                },
                modifier = Modifier.clickable { onPlaylistClick(playlist.id) }
            )
        }
    }
}

@Composable
fun EmptyState(title: String, subtitle: String, icon: ImageVector) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun CreatePlaylistDialog(onDismiss: () -> Unit, onCreate: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Playlist") },
        text = { OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Playlist name") }, singleLine = true) },
        confirmButton = { TextButton(onClick = { if (name.isNotBlank()) onCreate(name.trim()) }, enabled = name.isNotBlank()) { Text("Create") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
