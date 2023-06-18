package com.barinov.simpleplayer.domain

import android.media.session.MediaSession
import com.barinov.simpleplayer.core.MediaController
import kotlinx.coroutines.flow.SharedFlow

abstract class MediaControl(): MediaSession.Callback(){

    protected var onStatusChanged: ((Int) -> Unit)? = null

    fun setOnStatusChangeCallBack(onStatusChanged: (Int) -> Unit) {
        this.onStatusChanged = onStatusChanged
    }

    abstract val mediaSessionEventFlow: SharedFlow<MediaController.MediaSessionEvents>

//    fun setPause(directly: Boolean = false)
//
//    fun setPlay(directly: Boolean = false)
//
//    fun setStop(directly: Boolean = false)
//
//    fun setNext(directly: Boolean = false)
//
//    fun setPrev(directly: Boolean = false)
}