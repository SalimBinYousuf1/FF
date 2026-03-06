package com.vibeplayer.viewmodel

import android.content.ComponentName
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.vibeplayer.data.model.*
import com.vibeplayer.data.repository.MusicRepository
import com.vibeplayer.service.MusicPlaybackService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    val repository: MusicRepository
) : ViewModel() {

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val _isServiceConnected = MutableStateFlow(false)
    val isServiceConnected: StateFlow<Boolean> = _isServiceConnected.asStateFlow()

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null

    init {
        connectToService()
        startProgressTracking()
    }

    private fun connectToService() {
        val sessionToken = SessionToken(
            context,
            ComponentName(context, MusicPlaybackService::class.java)
        )
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener({
            controller = controllerFuture?.get()
            _isServiceConnected.value = true
            setupPlayerListeners()
        }, MoreExecutors.directExecutor())
    }

    private fun setupPlayerListeners() {
        controller?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                updateState()
            }
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playerState.update { it.copy(isPlaying = isPlaying) }
            }
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                updateCurrentSong()
            }
            override fun onRepeatModeChanged(repeatMode: Int) {
                _playerState.update {
                    it.copy(
                        repeatMode = when (repeatMode) {
                            Player.REPEAT_MODE_ONE -> RepeatMode.ONE
                            Player.REPEAT_MODE_ALL -> RepeatMode.ALL
                            else -> RepeatMode.OFF
                        }
                    )
                }
            }
            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                _playerState.update {
                    it.copy(shuffleMode = if (shuffleModeEnabled) ShuffleMode.ON else ShuffleMode.OFF)
                }
            }
        })
    }

    private fun updateState() {
        val ctrl = controller ?: return
        _playerState.update {
            it.copy(
                isPlaying = ctrl.isPlaying,
                duration = ctrl.duration.coerceAtLeast(0),
                isBuffering = ctrl.playbackState == Player.STATE_BUFFERING
            )
        }
        updateCurrentSong()
    }

    private fun updateCurrentSong() {
        // Current song is tracked via the queue
        val ctrl = controller ?: return
        val idx = ctrl.currentMediaItemIndex
        val queue = _playerState.value.queue
        if (idx in queue.indices) {
            _playerState.update { it.copy(currentSong = queue[idx], currentQueueIndex = idx) }
        }
    }

    private fun startProgressTracking() {
        viewModelScope.launch {
            while (true) {
                delay(500)
                controller?.let { ctrl ->
                    if (ctrl.isPlaying) {
                        _playerState.update { it.copy(progress = ctrl.currentPosition.coerceAtLeast(0)) }
                    }
                }
            }
        }
    }

    fun playSong(song: Song, queue: List<Song> = listOf(song), startIndex: Int = 0) {
        val ctrl = controller ?: return
        val mediaItems = queue.map { s ->
            MediaItem.Builder()
                .setUri(s.uri)
                .setMediaId(s.id.toString())
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(s.title)
                        .setArtist(s.artist)
                        .setAlbumTitle(s.album)
                        .setArtworkUri(s.albumArtUri?.let { android.net.Uri.parse(it) })
                        .build()
                )
                .build()
        }
        ctrl.setMediaItems(mediaItems, startIndex, 0)
        ctrl.prepare()
        ctrl.play()
        _playerState.update {
            it.copy(
                queue = queue,
                currentSong = queue.getOrNull(startIndex),
                currentQueueIndex = startIndex
            )
        }
        viewModelScope.launch {
            repository.incrementPlayCount(song.id)
        }
    }

    fun playPause() {
        val ctrl = controller ?: return
        if (ctrl.isPlaying) ctrl.pause() else ctrl.play()
    }

    fun skipNext() {
        controller?.seekToNextMediaItem()
        updateCurrentSong()
    }

    fun skipPrevious() {
        val ctrl = controller ?: return
        if ((ctrl.currentPosition) > 3000) {
            ctrl.seekTo(0)
        } else {
            ctrl.seekToPreviousMediaItem()
        }
        updateCurrentSong()
    }

    fun seekTo(position: Long) {
        controller?.seekTo(position)
        _playerState.update { it.copy(progress = position) }
    }

    fun toggleRepeat() {
        val ctrl = controller ?: return
        val newMode = when (ctrl.repeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            else -> Player.REPEAT_MODE_OFF
        }
        ctrl.repeatMode = newMode
    }

    fun toggleShuffle() {
        val ctrl = controller ?: return
        ctrl.shuffleModeEnabled = !ctrl.shuffleModeEnabled
    }

    fun addToQueue(song: Song) {
        val ctrl = controller ?: return
        val mediaItem = MediaItem.Builder()
            .setUri(song.uri)
            .setMediaId(song.id.toString())
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(song.title)
                    .setArtist(song.artist)
                    .setAlbumTitle(song.album)
                    .build()
            )
            .build()
        ctrl.addMediaItem(mediaItem)
        _playerState.update { it.copy(queue = it.queue + song) }
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            repository.toggleFavorite(song)
        }
    }

    override fun onCleared() {
        controllerFuture?.let { MediaController.releaseFuture(it) }
        super.onCleared()
    }
}
