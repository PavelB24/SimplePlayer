package com.barinov.simpleplayer.core

import android.media.session.MediaSession
import com.barinov.simpleplayer.domain.MediaSessionAdapter
import com.barinov.simpleplayer.domain.MusicRepository

class MediaSessionEventHandler(
    private val mediaEngine: MediaEngine,
    private val trackRepository: MusicRepository,
    private val mediaSession: MediaSession,
    private val mediaSessionAdapter: MediaSessionAdapter
    ): MediaSession.Callback() {

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