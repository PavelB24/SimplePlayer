package com.barinov.simpleplayer.domain

import android.media.MediaDataSource
import android.media.MediaMetadata
import android.media.MediaMetadataRetriever
import android.media.MediaParser
import android.media.MediaPlayer
import android.media.browse.MediaBrowser
import android.service.media.MediaBrowserService
import com.barinov.simpleplayer.BuildConfig
import java.io.File

class AudioDataHandler {


    private val retriever = MediaMetadataRetriever()

    fun getDuration(musicFile: File): Int? {
        return try {
            retriever.run {
                setDataSource(musicFile.path)
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toInt()
            }
        } catch (e: Exception){
            if(BuildConfig.DEBUG){
                e.printStackTrace()
            }
            null
        }
    }


    fun getMusicFileMetaData(musicFile: File){
        retriever.setDataSource(musicFile.path)
        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)
        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE)
    }

}