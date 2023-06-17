package com.barinov.simpleplayer.domain

import com.barinov.simpleplayer.domain.model.MusicFile

interface TrackRemover: Player {

    fun deleteFromCurrentPlayBack(mFile: MusicFile)

}