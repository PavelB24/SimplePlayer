package com.barinov.simpleplayer.core

import android.media.AudioAttributes
import android.media.MediaPlayer
import com.barinov.simpleplayer.domain.RepeatType
import com.barinov.simpleplayer.domain.TrackRemover
import com.barinov.simpleplayer.domain.model.MusicFile
import com.barinov.simpleplayer.indexOrNull
import com.barinov.simpleplayer.prefs.PreferencesManager
import com.barinov.simpleplayer.musicFileIterator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Random

class MediaEngine(
    private val preferencesManager: PreferencesManager
) : TrackRemover {

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

//    private val _playerEventFlow = MutableSharedFlow<PlayerEvents>()
//    private val playerEventFlow = _playerEventFlow.asSharedFlow()
    private val playerScope = CoroutineScope(Job() + Dispatchers.IO)
    private val mutex = Mutex()

    private fun setDataSource(
        musicFile: MusicFile
    ) {
        mediaPlayer.setDataSource(musicFile.file.path)
        mediaPlayer.prepare()
    }

    override fun getCurrentPosition() = musicFileIterator.getCurrentPosition()

    override fun startMusic(
        musicFile: MusicFile,
        notBindToPlaylist: Boolean
    ) {
        startMusic(listOf(musicFile), musicFile.id, notBindToPlaylist)
    }

    override fun getCurrentTrackId(): String? = musicFileIterator.currentTrackId




    fun startMusic(
        musicFiles: List<MusicFile>,
        startId: String,
        notBindToPlaylist: Boolean
    ) {
        mediaPlayer.reset()
        if (checkPlaylists(musicFiles.firstOrNull()?.playlistId)) {
            if (musicFileIterator.currentTrackId == startId) {
                resume()
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
                    playerScope.launch {
                        mutex.withLock {
                            onCompleteTrack()
                        }
                    }
                }
                setDataSource(musicFileIterator.next())
                mediaPlayer.start()
//                playerScope.launch {
//                    _playerEventFlow.emit(PlayerEvents.PlayingStarted(getCurrentTrackId()))
//                }
            }
        }
    }

    private suspend fun onCompleteTrack() {
        if (!mediaPlayer.isLooping) {
            when (repeatType) {
                RepeatType.NONE -> {
                    if (musicFileIterator.hasNext()) {
                        setDataSource(musicFileIterator.next())
                        mediaPlayer.start()
//                        _playerEventFlow.emit(PlayerEvents.PlayingStarted(getCurrentTrackId()))
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
        musicFileIterator.currentPlayListId == playlistId


    fun nextTrack() {
        if (musicToPlay.isNotEmpty()) {
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

    fun previousTrack() {
        if (musicToPlay.isNotEmpty()) {
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

    override fun pause() {
        if (mediaPlayer.isPlaying) mediaPlayer.pause()
//        playerScope.launch {
//            _playerEventFlow.emit(PlayerEvents.PlayPaused(getCurrentTrackId()))
//        }
    }


    override fun resume() {
        if (!mediaPlayer.isPlaying) mediaPlayer.start()
    }


    private fun resetList() {
        musicToPlay = listOf()
        musicFileIterator = musicToPlay.musicFileIterator()
    }

    override fun stop() {
        mediaPlayer.stop()
//        playerScope.launch {
//            _playerEventFlow.emit(PlayerEvents.PlayStopped)
//        }
        mediaPlayer.reset()
        resetList()
    }


    fun setRepeatType(repeatType: RepeatType) {
        this.repeatType = repeatType
        preferencesManager.repeatTypeOrdinal = repeatType.ordinal
        when (repeatType) {
            RepeatType.ONE -> setTrackRepeat(true)
            else -> {
                setTrackRepeat(false)
            }
        }

    }

    private fun setTrackRepeat(repeat: Boolean) {
        mediaPlayer.isLooping = repeat
    }

    override fun deleteFromCurrentPlayBack(mFile: MusicFile) {
        playerScope.launch {
            mutex.withLock {
                if (musicFileIterator.currentPlayListId != mFile.playlistId) {
                    return@launch
                } else {
                    val currPos = musicFileIterator.getCurrentPosition()
                    if (mFile.id == getCurrentTrackId()) {
                        mediaPlayer.stop()
                    }
                    if (musicToPlay.size > 1) {
                        musicToPlay = musicToPlay.toMutableList().also { it.removeAt(currPos) }
                        musicFileIterator = musicToPlay.musicFileIterator()
                            .also { it.setCurrentPosition(if (currPos >= musicToPlay.size) currPos else currPos - 1) }
                        if (musicFileIterator.hasNext()) {
                            nextTrack()
                        } else {
                            previousTrack()
                        }
                    } else {
                        resetList()
                    }
                }
            }
        }
    }

//    sealed class PlayerEvents(){
//        data class PlayingStarted(val trackId: String?): PlayerEvents()
//
//        data class  PlayPaused(val trackId: String?): PlayerEvents()
//
//        object PlayStopped: PlayerEvents()
//    }



}
