package com.barinov.simpleplayer

import android.media.MediaPlayer
import java.io.File

class MediaEngine {

    val mediaPlayer = MediaPlayer()



    fun setDataSource(musicFilePath: String){
        mediaPlayer.setDataSource(musicFilePath)
        mediaPlayer.prepare()
    }


}
