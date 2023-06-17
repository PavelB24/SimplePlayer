package com.barinov.simpleplayer.domain

import android.media.MediaDataSource
import android.media.MediaMetadata
import android.media.MediaMetadataRetriever
import android.media.MediaParser
import android.media.MediaPlayer
import android.media.browse.MediaBrowser
import android.service.media.MediaBrowserService
import com.barinov.simpleplayer.BuildConfig
import com.barinov.simpleplayer.domain.model.MusicFileMetaData
import java.io.File

class AudioDataHandler {


    private val retriever = MediaMetadataRetriever()

    fun getDuration(musicFile: File): Int? {
        return try {
            retriever.run {
                setDataSource(musicFile.path)
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toInt()
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            null
        }
    }


    fun getMusicFileMetaData(musicFile: File): MusicFileMetaData {
        return retriever.run {
            setDataSource(musicFile.path)
            MusicFileMetaData(
                extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM),
                extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST),
                extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE),
                extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong(),
                extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE),
                extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE)
            )
        }
    }

}