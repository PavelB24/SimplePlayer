package com.barinov.simpleplayer.service

import android.app.Service
import android.content.Intent
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.IBinder
import android.support.v4.media.session.PlaybackStateCompat
import com.barinov.simpleplayer.domain.MediaSessionAdapter
import org.koin.android.ext.android.inject


class PlayerMediaService : Service() {


    private val mediaSession by lazy { MediaSession(this, "SimplePlayer") }
    private val mediaSessionAdapter: MediaSessionAdapter by inject()

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