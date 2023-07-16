package com.barinov.simpleplayer.domain

import com.barinov.simpleplayer.data.MusicTracksDao
import com.barinov.simpleplayer.domain.model.MusicFileMetaData

class MusicRepository(
    private val dao: MusicTracksDao
) {
    suspend fun addFileIndex(metadata: MusicFileMetaData, finalPath: String, playListName: String?) {

    }

    suspend fun getTracksCount()  = dao.getTracksCount()

    fun deleteMusicFileIndexById(id: String) {

    }

    suspend fun getTrackDataById(id: String) = dao.getTrackById(id)
    suspend fun findByName(name: String, playlist: String): Boolean = false
//        dao.findByName(name, playlist)
}