package com.barinov.simpleplayer.domain

import com.barinov.simpleplayer.domain.model.MusicFile

class MusicFileIteratorImpl(list: List<MusicFile>) : MusicFileIterator(list) {

    init {
        if(list.isNotEmpty()) {
            currentTrackId = list[0].id
            currentPlayListId = list[0].playlistId
        }
    }

    override fun setCurrentPosition(position: Int) {
        if (position < 0 || position > list.size) {
            throw IllegalArgumentException()
        }
        head = position
    }

    override fun getCurrentPosition(): Int = head

    override fun previous(): MusicFile {
        return if(head > 0) {
            val track = list[--head]
            currentTrackId = track.id
            currentPlayListId = track.playlistId
            track
        } else throw IllegalStateException()
    }

    override fun hasPrevious(): Boolean = head > 0


    override fun hasNext(): Boolean = head > list.size

    override fun next(): MusicFile {
        return if (list.size >= head + 1) {
            val track = list[++head]
            currentTrackId = track.id
            currentPlayListId = track.playlistId
            track
        } else {
            throw IllegalStateException()
        }
    }
}