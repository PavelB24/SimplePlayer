package com.barinov.simpleplayer.data

import android.media.MediaPlayer.TrackInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.barinov.simpleplayer.domain.model.MusicFileMetadataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicTracksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackData(track: MusicFileMetadataEntity)

    @Query("DELETE FROM tracks WHERE id =:id")
    suspend fun deleteTrackDataById(id: String)

    @Query("SELECT * FROM tracks WHERE id =:id limit 1")
    suspend fun getTrackById(id: String): MusicFileMetadataEntity?

    @Query("SELECT COUNT(*) FROM tracks")
    fun getTracksCountFlow(): Flow<Int>

}