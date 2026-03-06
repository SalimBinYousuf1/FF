package com.vibeplayer.data.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class Song(
    @PrimaryKey
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val duration: Long,        // in milliseconds
    val size: Long,            // in bytes
    val uri: String,
    val albumArtUri: String?,
    val trackNumber: Int,
    val year: Int,
    val dateAdded: Long,
    val isFavorite: Boolean = false,
    val playCount: Int = 0,
    val lastPlayed: Long = 0L
)

data class Album(
    val id: Long,
    val name: String,
    val artist: String,
    val songCount: Int,
    val albumArtUri: String?,
    val year: Int
)

data class Artist(
    val id: Long,
    val name: String,
    val albumCount: Int,
    val songCount: Int
)

@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "playlist_songs", primaryKeys = ["playlistId", "songId"])
data class PlaylistSongCrossRef(
    val playlistId: Long,
    val songId: Long,
    val position: Int = 0,
    val addedAt: Long = System.currentTimeMillis()
)

data class PlaylistWithSongs(
    val playlist: Playlist,
    val songs: List<Song>
)

enum class RepeatMode { OFF, ONE, ALL }
enum class ShuffleMode { OFF, ON }
enum class SortBy { TITLE, ARTIST, ALBUM, DATE_ADDED, DURATION, PLAY_COUNT }
enum class SortOrder { ASCENDING, DESCENDING }

data class PlayerState(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val progress: Long = 0L,
    val duration: Long = 0L,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val shuffleMode: ShuffleMode = ShuffleMode.OFF,
    val queue: List<Song> = emptyList(),
    val currentQueueIndex: Int = 0,
    val isBuffering: Boolean = false
)

data class AppSettings(
    val darkTheme: Boolean = true,
    val dynamicColor: Boolean = true,
    val defaultTab: Int = 0,
    val sortBy: SortBy = SortBy.TITLE,
    val sortOrder: SortOrder = SortOrder.ASCENDING,
    val equalizer: EqualizerSettings = EqualizerSettings()
)

data class EqualizerSettings(
    val enabled: Boolean = false,
    val bassBoost: Int = 0,    // 0-1000 millibels
    val virtualizer: Int = 0,  // 0-1000
    val bands: List<Int> = List(5) { 0 } // -1500 to +1500 millibels per band
)
