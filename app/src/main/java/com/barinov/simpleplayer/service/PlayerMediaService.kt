package com.barinov.simpleplayer.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.browse.MediaBrowser
import android.media.session.PlaybackState
import android.os.Bundle
import android.os.IBinder
import android.service.media.MediaBrowserService
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.session.MediaButtonReceiver
import com.barinov.simpleplayer.NotificationStyleHelper
import com.barinov.simpleplayer.R
import com.barinov.simpleplayer.completeStyling
import com.barinov.simpleplayer.core.MediaController
import com.barinov.simpleplayer.ui.MainActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.ParametersHolder


class PlayerMediaService : MediaBrowserService() {

    companion object {
        const val NOTIFICATION_CHANNEL_NAME = "player_channel"
        const val NOTIFICATION_ID = 777
        const val MEDIA_SESSION_TAG = "SimplePlayer"
    }


    private val mediaSession by lazy { MediaSessionCompat(this, MEDIA_SESSION_TAG) }
    private val mediaSessionAdapter: MediaController by inject {
        ParametersHolder(mutableListOf(mediaSession))
    }

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        TODO("Not yet implemented")
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowser.MediaItem>>
    ) {
        TODO("Not yet implemented")
    }


    private fun onStatusChanged(status: Int) {
        when (status) {
            PlaybackState.STATE_PLAYING -> {
                startForeground(NOTIFICATION_ID, createNotification(status))
            }
            PlaybackState.STATE_PAUSED -> {
                startForeground(NOTIFICATION_ID, createNotification(status))
            }
            else -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
            }
        }
    }

    private fun createNotification(status: Int): Notification {
        return NotificationStyleHelper.from(
            NOTIFICATION_CHANNEL_NAME,
            this,
            mediaSession
        )
            .completeStyling(this, status, mediaSession.sessionToken)
            .build()

    }

    override fun onCreate() {
        super.onCreate()
        @SuppressLint("WrongConstant") val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_NAME,
            getString(R.string.notification_channel),
            NotificationManagerCompat.IMPORTANCE_DEFAULT
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)

        /**
         * Deprecated
         */
//        mediaSession.setFlags(
//            MediaSession.FLAG_HANDLES_MEDIA_BUTTONS
//                    or MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS
//        )
        mediaSession.let {
            it.setCallback(mediaSessionAdapter.also { controller ->
                controller.setOnStatusChangeCallBack(this::onStatusChanged)
            })

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                it.setMediaButtonReceiver(
//                    ComponentName(
//                        this,
//                        MediaButtonSignalReceiver::class.java
//                    )
//                )
//            } else {
            it.setMediaButtonReceiver(
                PendingIntent.getBroadcast(
                    this, 0,
                    Intent(
                        Intent.ACTION_MEDIA_BUTTON, null, this, MediaButtonReceiver::class.java
                    ),
                    0
                )
            )
//            }
            it.setSessionActivity(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(
                        this,
                        MainActivity::class.java
                    ),
                    0
                )
            )
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(mediaSession, intent);
        return START_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
        mediaSession.release()
    }
}