package com.barinov.simpleplayer

import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.media.session.MediaButtonReceiver
import com.barinov.simpleplayer.domain.MusicFileIterator
import com.barinov.simpleplayer.domain.MusicFileIteratorImpl
import com.barinov.simpleplayer.domain.RootType
import com.barinov.simpleplayer.domain.model.CommonFileItem
import com.barinov.simpleplayer.domain.model.MusicFile
import com.barinov.simpleplayer.service.PlayerMediaService
import com.barinov.simpleplayer.ui.ColorsContainer
import com.barinov.simpleplayer.ui.SystemColorsContainer
import com.barinov.simpleplayer.ui.primary_color
import me.jahnen.libaums.core.fs.FileSystem
import me.jahnen.libaums.core.fs.UsbFile
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import kotlin.math.roundToInt


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

fun File.toCommonFileItem(): CommonFileItem {
    val signature = this.path + File.separator + name
    return CommonFileItem(signature, length(), RootType.INTERNAL, this, null)
}

fun CommonFileItem.extractPath(): String{
    return if(rootType == RootType.INTERNAL){
        iFile?.path ?: throw IllegalArgumentException()
    } else {
        uEntity?.uFile?.absolutePath ?: throw IllegalArgumentException()
    }
}

fun UsbFile.toCommonFileItem(fs: FileSystem): CommonFileItem {
    val signature = this.absolutePath + File.separator + name
    return CommonFileItem(signature, length, RootType.USB, null, CommonFileItem.UsbData(this, fs))
}

fun ColorsContainer.toSystemColorsContainer(): SystemColorsContainer{
    return SystemColorsContainer(systemTopUiColor, navBarColor)
}

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
        color = primary_color.toArgb()
        setShowWhen(false)
//        builder.setPriority(NotificationCompat.PRIORITY_HIGH)
        setOnlyAlertOnce(true)
        setChannelId(PlayerMediaService.NOTIFICATION_CHANNEL_NAME)
    }
}

fun CommonFileItem.isFile(): Boolean{
    return if(rootType == RootType.INTERNAL){
        iFile!!.isFile
    } else {
        uEntity!!.uFile.isDirectory
    }
}

fun CommonFileItem.getSize(): Long{
    return if(rootType == RootType.INTERNAL){
        iFile!!.length()
    } else {
        uEntity!!.uFile.length
    }
}

fun Long.sizeBytesToMb(): String{
    return  "${String.format("%.1f", this / (1024.0 * 1024.0))}.mb"
}

// Len w/o dots
fun String.ellipsizePath(maxLen: Int): String{
    if(length <= maxLen) return this
    for (i in indices){
        if(this[i] == '/'){
            if(length - i <= maxLen){
                return takeLast(length - i)
            }
        }
    }
    return takeLast(maxLen) + "..."
}

fun CommonFileItem.getName(): String{
    return if(rootType == RootType.INTERNAL){
        iFile!!.name
    } else uEntity!!.uFile.name
}

fun Long.bytesToMb(): Int{
    return (this / (1024 * 1024.0f)).roundToInt()
}

inline fun InputStream.copyWithCallBack(
    alreadyCopied: Long,
    out: OutputStream,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    onBlockCopied:  (Int) -> Unit
): Long {
    var bytesCopied: Long = alreadyCopied
    val buffer = ByteArray(bufferSize)
    var bytes = read(buffer)
    while (bytes >= 0) {
        out.write(buffer, 0, bytes)
        val old = bytesCopied
        bytesCopied += bytes
        if (old.bytesToMb() !=  bytesCopied.bytesToMb()){
            onBlockCopied.invoke(bytesCopied.bytesToMb())
        }
        bytes = read(buffer)
    }
    return bytesCopied
}