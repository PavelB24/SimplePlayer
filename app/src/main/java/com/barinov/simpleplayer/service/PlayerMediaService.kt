package com.barinov.simpleplayer.service

import android.app.Notification
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.media.browse.MediaBrowser
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.service.media.MediaBrowserService
import androidx.media.session.MediaButtonReceiver
import com.barinov.simpleplayer.NotificationStyleHelper
import com.barinov.simpleplayer.broadcastReceivers.MediaButtonSignalReceiver
import com.barinov.simpleplayer.core.MediaController
import com.barinov.simpleplayer.ui.MainActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.ParametersHolder


class PlayerMediaService : MediaBrowserService() {


    private val mediaSession by lazy { MediaSession(this, "SimplePlayer") }
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


    private fun onStatusChanged(status: Int){
        when(status){
            PlaybackState.STATE_PLAYING->{

            }
            PlaybackState.STATE_PAUSED->{

            }
            else ->{
                stopForeground(STOP_FOREGROUND_REMOVE)
            }
        }
    }

    private fun createNotification(): Notification{
        NotificationStyleHelper.from()
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
        mediaSession.let {
            it.setCallback(mediaSessionAdapter.also { controller->
                controller.setOnStatusChangeCallBack(this::onStatusChanged)
            })
            val activityIntent = Intent(this, MainActivity::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                it.setMediaButtonBroadcastReceiver(
                    ComponentName(
                        this,
                        MediaButtonSignalReceiver::class.java
                    )
                )
            } else {
                it.setMediaButtonReceiver(
                    PendingIntent.getBroadcast(
                        this, 0,
                        Intent(
                            Intent.ACTION_MEDIA_BUTTON, null, this, MediaButtonReceiver::class.java
                        ),
                        0
                    )
                )
            }
            it.setSessionActivity(
                PendingIntent.getActivity(this, 0, activityIntent, 0)
            )
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mediaSession.release()
    }
}