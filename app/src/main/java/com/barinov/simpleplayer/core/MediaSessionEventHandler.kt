package com.barinov.simpleplayer.core

import android.media.session.MediaSession
import android.media.session.PlaybackState
import com.barinov.simpleplayer.domain.MediaSessionAdapter
import com.barinov.simpleplayer.domain.MusicRepository

class MediaSessionEventHandler(
    private val mediaEngine: MediaEngine,
    private val trackRepository: MusicRepository,
    private val mediaSession: MediaSession,
    private val mediaSessionAdapter: MediaSessionAdapter
    ): MediaSession.Callback() {

    private val playBackState = PlaybackState.Builder().setActions(
        PlaybackState.ACTION_PLAY
                or PlaybackState.ACTION_STOP
                or PlaybackState.ACTION_PAUSE
                or PlaybackState.ACTION_PLAY_PAUSE
                or PlaybackState.ACTION_SKIP_TO_NEXT
                or PlaybackState.ACTION_SKIP_TO_PREVIOUS
    )

    override fun onPlay() {
        mediaEngine.getCurrentTrackId()
        super.onPlay()
    }


    override fun onPause() {
        super.onPause()
    }


    override fun onSkipToNext() {
        super.onSkipToNext()
    }


    override fun onStop() {
        super.onStop()
    }

    override fun onSkipToPrevious() {
        super.onSkipToPrevious()
    }




}