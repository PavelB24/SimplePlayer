package com.barinov.simpleplayer.domain

import android.media.MediaMetadataRetriever
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


    fun getShortSignature(musicFile: File,  setSource: Boolean = true): FileWorker.Signature {
        if (setSource) retriever.setDataSource(musicFile.path)
        return FileWorker.Signature(
            getTitle(musicFile),
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE) ?: "0",
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0,
        )
    }

    private fun getTitle(musicFile: File,): String {
        return (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            ?: musicFile.name.split(".").run { take(this.size - 1) }.joinToString { it })
    }

    fun getMusicFileMetaData(musicFile: File): MusicFileMetaData {
        return retriever.run {
            setDataSource(musicFile.path)
            MusicFileMetaData(
                getTitle(musicFile),
                embeddedPicture,
                extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM),
                extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST),
                extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE),
                extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0,
                extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE),
                extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE)
            )
        }
    }

}