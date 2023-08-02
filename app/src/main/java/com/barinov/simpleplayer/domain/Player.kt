package com.barinov.simpleplayer.domain

import com.barinov.simpleplayer.domain.model.MusicFile

interface Player {

    fun getCurrentTrackId(): String?

    fun getCurrentPosition(): Int

    fun resume()

    fun startMusic(
        musicFile: MusicFile,
        notBindToPlaylist: Boolean
    )

    fun pause()

    fun stop()

}