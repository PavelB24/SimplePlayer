package com.barinov.simpleplayer.domain

import com.barinov.simpleplayer.domain.model.MusicFile

abstract class MusicFileIterator(protected val list: List<MusicFile>): RandomAccessIterator<MusicFile> {


    protected var head: Int = 0

    protected var currentTrackId: String? = null
    protected var currentPlayListId: String? = null


    fun getCurrentTrackId(): String? = currentTrackId

    fun getCurrentPlaylistId(): String? = currentPlayListId

}