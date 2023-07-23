package com.barinov.simpleplayer.domain

import com.barinov.simpleplayer.domain.model.MusicFileMetaData
import com.barinov.simpleplayer.domain.model.PlaylistEntity
import com.barinov.simpleplayer.domain.model.TrackEntity
import java.util.UUID

class StorageDataCreator {

    fun createTrackEntity(metaData: MusicFileMetaData, path: String, playlistId: String, storageType: MusicStorageType): TrackEntity{
        return metaData.run {
            TrackEntity(
                UUID.randomUUID().toString(),
                title,
                cover,
                playlistId,
                path,
                storageType,
                album,
                artist,
                bitrate,
                duration ?: -1,
                genre,
                date
            )
        }
    }

    fun createPlaylistEntity(playListName: String): PlaylistEntity {
        return PlaylistEntity(UUID.randomUUID().toString(), playListName)
    }
}