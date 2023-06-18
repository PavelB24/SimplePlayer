package com.barinov.simpleplayer

import android.app.Notification
import android.content.Context
import android.media.session.MediaSession
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.session.MediaButtonReceiver
import com.barinov.simpleplayer.broadcastReceivers.MediaButtonSignalReceiver

object NotificationStyleHelper {

    fun from(channelId: String,
             context: Context,  mediaSession: MediaSession
    ): Notification.Builder {
        val controller = mediaSession.controller
        val mediaMetadata = controller.metadata
        val description = mediaMetadata?.description


        return Notification.Builder(context, channelId).apply {
            setContentTitle(description?.title)
            setContentText(description?.subtitle)
            setSubText(description?.description)
            setLargeIcon(description?.iconBitmap)
            setContentIntent(controller.sessionActivity)
            setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    context,
                    PlaybackStateCompat.ACTION_STOP
                )
            )
                setVisibility(Notification.VISIBILITY_PUBLIC)

        }
    }

}