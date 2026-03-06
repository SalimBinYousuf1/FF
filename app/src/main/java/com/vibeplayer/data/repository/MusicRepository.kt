package com.vibeplayer.data.repository

import com.vibeplayer.data.local.MediaStoreScanner
import com.vibeplayer.data.local.PlaylistDao
import com.vibeplayer.data.local.SongDao
import com.vibeplayer.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepository @Inject constructor(
    private val songDao: SongDao,
    private val playlistDao: PlaylistDao,
    private val scanner: MediaStoreScanner
) {
    // Albums & Artists cached in memory (from MediaStore)
    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums.asStateFlow()

    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists: StateFlow<List<Artist>> = _artists.asStateFlow()

    // Songs from Room (includes favorites, play counts etc.)
    fun getAllSongs(): Flow<List<Song>> = songDao.getAllSongs()
    fun getFavoriteSongs(): Flow<List<Song>> = songDao.getFavoriteSongs()
    fun getMostPlayedSongs(): Flow<List<Song>> = songDao.getMostPlayedSongs()
    fun getRecentlyAddedSongs(): Flow<List<Song>> = songDao.getRecentlyAddedSongs()
    fun getRecentlyPlayedSongs(): Flow<List<Song>> = songDao.getRecentlyPlayedSongs()
    fun searchSongs(query: String): Flow<List<Song>> = songDao.searchSongs(query)
    fun getSongsByAlbum(album: String): Flow<List<Song>> = songDao.getSongsByAlbum(album)
    fun getSongsByArtist(artist: String): Flow<List<Song>> = songDao.getSongsByArtist(artist)

    // Playlists
    fun getAllPlaylists(): Flow<List<Playlist>> = playlistDao.getAllPlaylists()
    fun getPlaylistSongs(playlistId: Long): Flow<List<Song>> = playlistDao.getPlaylistSongs(playlistId)
    fun getPlaylistSongCount(playlistId: Long): Flow<Int> = playlistDao.getPlaylistSongCount(playlistId)

    suspend fun syncWithMediaStore() {
        val songs = scanner.scanSongs()
        songDao.upsertSongs(songs)
        if (songs.isNotEmpty()) {
            songDao.deleteRemovedSongs(songs.map { it.id })
        }
        _albums.value = scanner.scanAlbums()
        _artists.value = scanner.scanArtists()
    }

    suspend fun toggleFavorite(song: Song) {
        songDao.updateFavorite(song.id, !song.isFavorite)
    }

    suspend fun incrementPlayCount(songId: Long) {
        songDao.incrementPlayCount(songId)
    }

    suspend fun createPlaylist(name: String): Long {
        return playlistDao.insertPlaylist(Playlist(name = name))
    }

    suspend fun renamePlaylist(playlist: Playlist, newName: String) {
        playlistDao.updatePlaylist(playlist.copy(name = newName, updatedAt = System.currentTimeMillis()))
    }

    suspend fun deletePlaylist(playlistId: Long) {
        playlistDao.deletePlaylist(playlistId)
        playlistDao.clearPlaylistSongs(playlistId)
    }

    suspend fun addSongToPlaylist(playlistId: Long, songId: Long, position: Int = 0) {
        playlistDao.addSongToPlaylist(PlaylistSongCrossRef(playlistId, songId, position))
    }

    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        playlistDao.removeSongFromPlaylist(playlistId, songId)
    }
}
