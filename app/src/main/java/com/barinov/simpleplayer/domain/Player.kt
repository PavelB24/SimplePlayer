package com.barinov.simpleplayer.domain

interface Player {

    fun getCurrentTrackId(): String?

    fun getCurrentPosition(): Int

    fun start()

    fun pause()

    fun stop()

}