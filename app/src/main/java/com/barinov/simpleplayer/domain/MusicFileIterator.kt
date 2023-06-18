package com.barinov.simpleplayer.domain

import com.barinov.simpleplayer.domain.model.MusicFile

abstract class MusicFileIterator(protected val list: List<MusicFile>): RandomAccessIterator<MusicFile> {


    protected var head: Int = 0

     var currentTrackId: String? = null
        protected set

     var currentPlayListId: String? = null
         protected set




}