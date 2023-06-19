package com.barinov.simpleplayer

import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.media.session.MediaButtonReceiver
import com.barinov.simpleplayer.domain.CoroutineFileWorker
import com.barinov.simpleplayer.domain.MusicFileIterator
import com.barinov.simpleplayer.domain.MusicFileIteratorImpl
import com.barinov.simpleplayer.domain.model.CommonFileItem
import com.barinov.simpleplayer.domain.model.MusicFile
import com.barinov.simpleplayer.service.PlayerMediaService
import com.barinov.simpleplayer.ui.primaryColor
import me.jahnen.libaums.core.fs.FileSystem
import me.jahnen.libaums.core.fs.UsbFile
import java.io.File
import java.io.InputStream
import java.io.OutputStream


fun List<MusicFile>.musicFileIterator(): MusicFileIterator {
    return MusicFileIteratorImpl(this)
}

inline fun <T> List<T>.indexOrNull(predicate: (T) -> Boolean): Int? {
    val iterator = this.listIterator()
    while (iterator.hasNext()) {
        if (predicate(iterator.next())) {
            return iterator.previousIndex()
        }
    }
    return null
}

fun File.toCommonFileItem() =
    CommonFileItem(CoroutineFileWorker.RootType.INTERNAL, this, null)

fun UsbFile.toCommonFileItem(fs: FileSystem) =
    CommonFileItem(CoroutineFileWorker.RootType.USB, null, CommonFileItem.UsbData(this, fs))

fun NotificationCompat.Builder.completeStyling(
    context: Context,
    status: Int,
    sessionToken: MediaSessionCompat.Token
): NotificationCompat.Builder {
    return apply {
        addAction(
            NotificationCompat.Action.Builder(
                IconCompat.createWithResource(context, android.R.drawable.ic_media_previous),
                context.getString(R.string.previous),
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    context,
                    PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                )
            ).build()
        )

        if (status == PlaybackStateCompat.STATE_PLAYING) {
            addAction(
                NotificationCompat.Action.Builder(
                    IconCompat.createWithResource(context, android.R.drawable.ic_media_pause),
                    context.getString(R.string.pause),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context,
                        PlaybackStateCompat.ACTION_PLAY_PAUSE
                    )
                ).build()
            )
        } else {
            addAction(
                NotificationCompat.Action.Builder(
                    IconCompat.createWithResource(context, android.R.drawable.ic_media_play),
                    context.getString(R.string.start),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context,
                        PlaybackStateCompat.ACTION_PLAY_PAUSE
                    )
                ).build()
            )
        }

        addAction(
            NotificationCompat.Action.Builder(
                IconCompat.createWithResource(context, android.R.drawable.ic_media_next),
                context.getString(R.string.next),
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    context,
                    PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                )
            ).build()
        )

        setStyle(
            androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(1)
                .setShowCancelButton(true)
                .setCancelButtonIntent(
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context,
                        PlaybackStateCompat.ACTION_STOP
                    )
                )
                .setMediaSession(sessionToken)
        ); // setMediaSession требуется для Android Wear
        setSmallIcon(R.mipmap.ic_launcher)
        color = primaryColor.toArgb()
        setShowWhen(false)
//        builder.setPriority(NotificationCompat.PRIORITY_HIGH)
        setOnlyAlertOnce(true)
        setChannelId(PlayerMediaService.NOTIFICATION_CHANNEL_NAME)
    }
}
suspend inline fun InputStream.copyWithCallBack(
    out: OutputStream,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    onBlockCopied: suspend (Long) -> Unit
): Long {
    var bytesCopied: Long = 0
    val buffer = ByteArray(bufferSize)
    var bytes = read(buffer)
    while (bytes >= 0) {
        out.write(buffer, 0, bytes)
        bytesCopied += bytes
        onBlockCopied.invoke(bytesCopied)
        bytes = read(buffer)
    }
    return bytesCopied
}