package com.barinov.simpleplayer.service

import android.app.Service
import android.content.Intent
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.IBinder
import android.support.v4.media.session.PlaybackStateCompat


class PlayerMediaService : Service() {


    private val mediaSession by lazy { MediaSession(this, "SimplePlayer") }
    private val playBackState = PlaybackState.Builder().setActions(
            PlaybackState.ACTION_PLAY
                or PlaybackState.ACTION_STOP
                or PlaybackState.ACTION_PAUSE
                or PlaybackState.ACTION_PLAY_PAUSE
                or PlaybackState.ACTION_SKIP_TO_NEXT
                or PlaybackState.ACTION_SKIP_TO_PREVIOUS
    )

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }


    override fun onCreate() {
        super.onCreate()
        /**
         * Deprecated
         */
//        mediaSession.setFlags(
//            MediaSession.FLAG_HANDLES_MEDIA_BUTTONS
//                    or MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS
//        )
        mediaSession.setCallback()

    }
}