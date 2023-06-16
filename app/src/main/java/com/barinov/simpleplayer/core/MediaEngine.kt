package com.barinov.simpleplayer.core

import android.media.AudioAttributes
import android.media.MediaPlayer
import com.barinov.simpleplayer.domain.RepeatType
import com.barinov.simpleplayer.domain.model.MusicFile
import com.barinov.simpleplayer.indexOrNull
import com.barinov.simpleplayer.prefs.PreferencesManager
import com.barinov.simpleplayer.musicFileIterator
import java.util.Random

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

    private var musicToPlay = listOf<MusicFile>()
    private var musicFileIterator = musicToPlay.musicFileIterator()
    private var repeatType = RepeatType.values()[preferencesManager.repeatTypeOrdinal]


    private fun setDataSource(
        musicFile: MusicFile
    ) {
        mediaPlayer.setDataSource(musicFile.file.path)
        mediaPlayer.prepare()
    }

    fun startMusic(
        musicFile: MusicFile
    ) {
        startMusic(listOf(musicFile), musicFile.id)
    }

    fun getCurrentTrackId(): String? = musicFileIterator.getCurrentTrackId()


    fun startMusic(
        musicFiles: List<MusicFile>,
        startId: String
    ) {
        mediaPlayer.reset()
        if (checkPlaylists(musicFiles.firstOrNull()?.playlistId)) {
            if(musicFileIterator.getCurrentTrackId() == startId){
                start()
            } else {
                val startFrom = musicToPlay.indexOrNull {
                    it.id == startId
                } ?: 0
                musicFileIterator.setCurrentPosition(startFrom)
            }
        } else {
            if (musicFiles.isNotEmpty()) {
                musicToPlay = musicFiles
                musicFileIterator = musicToPlay.musicFileIterator().also {
                    it.setCurrentPosition(musicToPlay.indexOrNull { it.id == startId } ?: 0)
                }
                mediaPlayer.setOnCompletionListener {
                    onCompleteTrack()
                }
                setDataSource(musicFileIterator.next())
                mediaPlayer.start()
            }
        }
    }

    private fun onCompleteTrack() {
        if (!mediaPlayer.isLooping) {
            when (repeatType) {
                RepeatType.NONE -> {
                    if (musicFileIterator.hasNext()) {
                        setDataSource(musicFileIterator.next())
                    } else {
                        mediaPlayer.reset()
                        resetList()
                    }
                }
                RepeatType.PLAYLIST -> {
                    if (musicFileIterator.hasNext()) {
                        setDataSource(musicFileIterator.next())
                    } else {
                        musicFileIterator = musicToPlay.musicFileIterator()
                        mediaPlayer.reset() //??
                        setDataSource(musicFileIterator.next())
                        mediaPlayer.start()
                    }
                }
                RepeatType.ONE -> {
                    throw IllegalStateException()
                }

                RepeatType.RANDOM -> {
                    var randomOrdinal = Random().nextInt(musicToPlay.size)
                    val current = musicToPlay.indexOrNull { it.id == musicToPlay[randomOrdinal].id }
                    if (randomOrdinal == current) {
                        randomOrdinal = 0
                    }
                    musicFileIterator.setCurrentPosition(randomOrdinal)
                    setDataSource(musicFileIterator.next())
                    mediaPlayer.start()
                }
            }
        }
    }


    private fun checkPlaylists(playlistId: String?) =
        musicFileIterator.getCurrentPlaylistId() == playlistId


    fun nextTrack() {
        if(musicToPlay.isNotEmpty()) {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            }
            if (musicFileIterator.hasNext()) {
                setDataSource(musicFileIterator.next())
            } else {
                musicFileIterator.setCurrentPosition(0)
                setDataSource(musicFileIterator.next())
            }
            mediaPlayer.start()
        }
    }

    fun previousTrack(){
        if(musicToPlay.isNotEmpty()) {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            }
            if (musicFileIterator.hasPrevious()) {
                setDataSource(musicFileIterator.previous())
            } else {
                musicFileIterator.setCurrentPosition(0)
                setDataSource(musicFileIterator.next())
            }
            mediaPlayer.start()
        }
    }

    fun release() {
        mediaPlayer.release()
    }

    fun pause() {
        if (mediaPlayer.isPlaying) mediaPlayer.pause()
    }


    fun start() {
        if (!mediaPlayer.isPlaying) mediaPlayer.start()
    }


    private fun resetList(){
        musicToPlay = listOf()
        musicFileIterator = musicToPlay.musicFileIterator()
    }

    fun stop(){
        mediaPlayer.stop()
        mediaPlayer.reset()
        resetList()
    }


    fun setRepeatType(repeatType: RepeatType) {
        this.repeatType = repeatType
        preferencesManager.repeatTypeOrdinal = repeatType.ordinal
        when (repeatType) {
            RepeatType.ONE -> setTrackRepeat(true)
            else -> {setTrackRepeat(false)}
        }

    }

    private fun setTrackRepeat(repeat: Boolean) {
        mediaPlayer.isLooping = repeat
    }


}
