package com.barinov.simpleplayer.domain

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.media.audiofx.EnvironmentalReverb
import com.barinov.simpleplayer.prefs.PreferencesManager
import java.io.File
import java.util.Queue
import java.util.Random
import java.util.concurrent.LinkedTransferQueue

class MediaEngine(
    private val preferencesManager: PreferencesManager
) {

    private val mediaPlayer = MediaPlayer().apply {
        setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
        setOnErrorListener { mp, what, extra ->
            mp.reset()
            true
        }
    }

    private var currentPlaylistId: String? = null
    private val musicToPlay = mutableListOf<File>()
    private var musicFileIterator = musicToPlay.listIterator()
    private var repeatType = RepeatType.values()[preferencesManager.repeatTypeOrdinal]


    private fun setDataSource(
        musicFile: File
    ) {
        mediaPlayer.setDataSource(musicFile.path)
        mediaPlayer.prepare()
    }


    suspend fun startMusic(
        playlistId: String,
        musicFiles: List<File>
    ) {
        if(musicToPlay.isNotEmpty()){
            musicToPlay.clear()
        }
        mediaPlayer.duration
        mediaPlayer.currentPosition
        if (musicFiles.isNotEmpty()) {
            musicToPlay.addAll(musicFiles)


            mediaPlayer.setOnCompletionListener {
                onCompleteTrack()
            }
            setDataSource(musicFileIterator.next())
        }
    }

    private fun onCompleteTrack() {
        if (!mediaPlayer.isLooping) {
            when(repeatType){
                RepeatType.NONE -> {
                    if (musicFileIterator.hasNext()) {
                        setDataSource(musicFileIterator.next())
                    } else {
                        mediaPlayer.reset()
                    }
                }
                RepeatType.PLAYLIST -> {
                    if (musicFileIterator.hasNext()) {
                        setDataSource(musicFileIterator.next())
                    } else {
                        musicFileIterator = musicToPlay.listIterator()
                        mediaPlayer.reset()
                        setDataSource(musicFileIterator.next())
                    }
                }
                RepeatType.ONE -> {
                    if(!mediaPlayer.isLooping){
                        throw IllegalStateException()
                    }
                }
                RepeatType.RANDOM -> {
                    val random = Random()

                }
            }
        }
    }


    fun checkPlaylists(playlistId: String) = currentPlaylistId == playlistId

    fun playSelectedDirectly(selectedTrack: File){

    }

    fun nextTrack(){
        mediaPlayer.apply {
            pause()

        }
    }

    fun release(){
        mediaPlayer.release()
    }

    fun pause() {
        if (mediaPlayer.isPlaying) mediaPlayer.pause()
    }


    fun start() {
        if (!mediaPlayer.isPlaying) mediaPlayer.start()
    }


    fun setRepeatType(repeatType: RepeatType) {
        preferencesManager.repeatTypeOrdinal = repeatType.ordinal
        when (repeatType) {
            RepeatType.NONE -> setTrackRepeat(false)
            RepeatType.PLAYLIST -> {
                setTrackRepeat(false)
                this.repeatType = repeatType
            }
            RepeatType.ONE -> setTrackRepeat(true)
        }

    }

    private fun setTrackRepeat(repeat: Boolean) {
        mediaPlayer.isLooping = repeat
    }


}
