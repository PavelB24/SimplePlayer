package com.barinov.simpleplayer

import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver

object NotificationStyleHelper {

    fun from(
        channelId: String,
        context: Context, mediaSession: MediaSessionCompat
    ): NotificationCompat.Builder {
        val controller = mediaSession.controller
        val mediaMetadata = controller.metadata
        val description = mediaMetadata?.description


        return NotificationCompat.Builder(context, channelId).apply {
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
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        }
    }


}