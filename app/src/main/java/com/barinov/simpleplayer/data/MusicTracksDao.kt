package com.barinov.simpleplayer.data

import androidx.room.Dao
import androidx.room.Query
import com.barinov.simpleplayer.domain.model.MusicFileMetadataEntity

@Dao
interface MusicTracksDao {

    suspend fun insertTrackData(track: MusicFileMetadataEntity)

    @Query("DELETE FROM tracks WHERE id =:id")
    suspend fun deleteTrackDataById(id: String)

}