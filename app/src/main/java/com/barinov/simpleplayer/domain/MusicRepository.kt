package com.barinov.simpleplayer.domain

import com.barinov.simpleplayer.data.MusicTracksDao
import com.barinov.simpleplayer.domain.model.CheckableTrackInfo
import com.barinov.simpleplayer.domain.model.PlaylistEntity
import com.barinov.simpleplayer.domain.model.TrackEntity

class MusicRepository(
    private val dao: MusicTracksDao
) {
    suspend fun addFileIndex(track: TrackEntity) {
        dao.insertTrackData(track)
    }

    suspend fun getTracksCount() = dao.getTracksCount()

    suspend fun deleteMusicFileIndexById(id: String) = dao.deleteTrackDataById(id)

    suspend fun getAllTrackPaths(): List<CheckableTrackInfo> = dao.getAllTrackPaths()

    suspend fun getTrackDataById(id: String) = dao.getTrackById(id)

    suspend fun findByName(name: String, playlist: String, bitrate: String, duration: Long): Boolean =
        dao.findByName(name, playlist, bitrate, duration) == 1

    suspend fun createPlayListIfNeedAndGetId(
        playListName: String,
        onNotFound: (String) -> PlaylistEntity
    ): String {
        val search = dao.getPlayListIdByName(playListName)
        return if (search != null) {
            search
        } else {
            val pl = onNotFound.invoke(playListName)
            dao.createPlaylist(pl)
            pl.id
        }
    }
}