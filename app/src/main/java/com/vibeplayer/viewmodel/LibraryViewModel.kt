package com.vibeplayer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vibeplayer.data.model.*
import com.vibeplayer.data.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LibraryUiState(
    val songs: List<Song> = emptyList(),
    val albums: List<Album> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val favoriteSongs: List<Song> = emptyList(),
    val recentlyPlayed: List<Song> = emptyList(),
    val mostPlayed: List<Song> = emptyList(),
    val recentlyAdded: List<Song> = emptyList(),
    val isLoading: Boolean = false,
    val isScanning: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState(isLoading = true))
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        loadLibrary()
        scanLibrary()
    }

    private fun loadLibrary() {
        viewModelScope.launch {
            combine(
                repository.getAllSongs(),
                repository.getFavoriteSongs(),
                repository.getRecentlyPlayedSongs(),
                repository.getMostPlayedSongs(),
                repository.getRecentlyAddedSongs(),
                repository.getAllPlaylists(),
                repository.albums,
                repository.artists
            ) { flows ->
                @Suppress("UNCHECKED_CAST")
                LibraryUiState(
                    songs = flows[0] as List<Song>,
                    favoriteSongs = flows[1] as List<Song>,
                    recentlyPlayed = flows[2] as List<Song>,
                    mostPlayed = flows[3] as List<Song>,
                    recentlyAdded = flows[4] as List<Song>,
                    playlists = flows[5] as List<Playlist>,
                    albums = flows[6] as List<Album>,
                    artists = flows[7] as List<Artist>,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun scanLibrary() {
        viewModelScope.launch {
            _uiState.update { it.copy(isScanning = true) }
            try {
                repository.syncWithMediaStore()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to scan library: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isScanning = false) }
            }
        }
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            repository.createPlaylist(name)
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            repository.deletePlaylist(playlistId)
        }
    }

    fun addSongToPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            repository.addSongToPlaylist(playlistId, songId)
        }
    }

    fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            repository.removeSongFromPlaylist(playlistId, songId)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
