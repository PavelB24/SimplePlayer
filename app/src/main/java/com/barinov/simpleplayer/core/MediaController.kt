package com.barinov.simpleplayer.core

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaMetadata
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.barinov.simpleplayer.domain.MediaControl
import com.barinov.simpleplayer.domain.MusicRepository
import com.barinov.simpleplayer.domain.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MediaController(
    context: Context,
    private val mediaEngine: Player,
    private val trackRepository: MusicRepository,
    private val mediaSession: MediaSessionCompat,
) : MediaControl() {

    private val playBackState = PlaybackStateCompat.Builder().setActions(
        PlaybackStateCompat.ACTION_PLAY
                or PlaybackStateCompat.ACTION_STOP
                or PlaybackStateCompat.ACTION_PAUSE
                or PlaybackStateCompat.ACTION_PLAY_PAUSE
                or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
    )

    private val audioManager = context.getSystemService(AudioManager::class.java)

    private val _mediaSessionEventFlow = MutableSharedFlow<MediaSessionEvents>()
    override val mediaSessionEventFlow = _mediaSessionEventFlow.asSharedFlow()

    private val handlerScope = CoroutineScope(Job() + Dispatchers.IO)


    private val audioAttr = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()

    private val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
        .setOnAudioFocusChangeListener(FocusListener())
        .setAcceptsDelayedFocusGain(false)
        .setWillPauseWhenDucked(true)
        .setAudioAttributes(audioAttr)
        .build()


    override fun onPlay() {
        val currTrack = mediaEngine.getCurrentTrackId()
        currTrack?.let {
            handlerScope.launch {
                trackRepository.getTrackDataById(it)?.let { data ->
                    val metaData = MediaMetadataCompat.Builder().run {
                        putString(MediaMetadata.METADATA_KEY_TITLE, data.title)
                        putString(MediaMetadata.METADATA_KEY_ALBUM, data.album)
                        putString(MediaMetadata.METADATA_KEY_ARTIST, data.artist)
                        putLong(MediaMetadata.METADATA_KEY_DURATION, data.duration)
                        putString(MediaMetadata.METADATA_KEY_GENRE, data.genre)
                        build()
                    }
                    mediaSession.let {
                        it.setMetadata(metaData)
                        setPlay()
                    }
                    mediaEngine.start()
                    _mediaSessionEventFlow.emit(MediaSessionEvents.Playing(mediaEngine.getCurrentTrackId()))
                    onStatusChanged?.invoke(PlaybackState.STATE_PLAYING)
                }

            }
        }
    }


    private fun setPause() {
        mediaSession.setPlaybackState(
            playBackState.setState(
                PlaybackStateCompat.STATE_PAUSED,
                mediaEngine.getCurrentPosition().toLong(), 1f
            ).build()
        )
    }

    private fun setPlay() {
        audioManager.requestAudioFocus(
            focusRequest
        )
        mediaSession.also {
            it.isActive = true
            it.setPlaybackState(
                playBackState.setState(
                    PlaybackStateCompat.STATE_PLAYING,
                    mediaEngine.getCurrentPosition().toLong(), 1f
                ).build()
            )
        }

    }

    private fun setStop() {
        mediaSession.setPlaybackState(
            playBackState.setState(
                PlaybackStateCompat.STATE_STOPPED,
                PlaybackState.PLAYBACK_POSITION_UNKNOWN, 1f
            ).build()
        )
    }

    private fun setNext() {}

    private fun setPrev() {}


    override fun onPause() {
        mediaEngine.pause()
        handlerScope.launch {
            _mediaSessionEventFlow.emit(MediaSessionEvents.Paused(mediaEngine.getCurrentTrackId()))
        }
        setPause()
        onStatusChanged?.invoke(PlaybackState.STATE_PAUSED)
    }


    override fun onSkipToNext() {
        super.onSkipToNext()
    }

    override fun onSkipToPrevious() {
        super.onSkipToPrevious()
    }


    override fun onStop() {
        mediaEngine.stop()
        handlerScope.launch {
            _mediaSessionEventFlow.emit(MediaSessionEvents.Stopped)
        }
        audioManager.abandonAudioFocusRequest(focusRequest)
        mediaSession.let {
            it.isActive = false
            setStop()
        }
        onStatusChanged?.invoke(PlaybackState.STATE_STOPPED)

    }

    sealed class MediaSessionEvents {
        data class Playing(val trackId: String?) : MediaSessionEvents()
        object Stopped : MediaSessionEvents()
        data class Paused(val trackId: String?) : MediaSessionEvents()
    }

    private inner class FocusListener() : AudioManager.OnAudioFocusChangeListener {

        override fun onAudioFocusChange(focusChange: Int) {
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN -> {
                    onPlay()
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    // Reduce the sound or pause
                }
                else -> {
                    onPause()
                }
            }
        }

    }


}