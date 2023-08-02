package com.barinov.simpleplayer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.barinov.simpleplayer.domain.model.CheckableTrackInfo
import com.barinov.simpleplayer.domain.model.PlaylistEntity
import com.barinov.simpleplayer.domain.model.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicTracksDao {

    @Query("SELECT id, path FROM tracks WHERE type == 1")
    suspend fun getAllTrackPaths(): List<CheckableTrackInfo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackData(track: TrackEntity)

    @Query("SELECT id FROM playlists WHERE name =:name")
    suspend fun getPlayListIdByName(name: String): String?

    @Query("DELETE FROM tracks WHERE id =:id")
    suspend fun deleteTrackDataById(id: String)

    @Query("SELECT * FROM tracks WHERE id =:id limit 1")
    suspend fun getTrackById(id: String): TrackEntity?

    @Query("SELECT COUNT(*) FROM tracks")
    suspend fun getTracksCount(): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun createPlaylist(playList: PlaylistEntity)


    @Query("SELECT COUNT(*) FROM tracks WHERE title =:name AND duration =:duration AND bitrate =:bitrate AND play_list_id == (SELECT id FROM playlists WHERE :playlistName == name)")
    suspend fun findByName(name: String, playlistName: String, bitrate: String, duration: Long): Int

    @Query("SELECT * FROM tracks")
    fun allTracks(): Flow<TrackEntity>
    @Query("SELECT * FROM tracks WHERE play_list_id =:id")
    fun getTracksByPlayListId(id: String): Flow<TrackEntity>

    @Query("SELECT * FROM playlists")
    fun allPlayLists(): Flow<PlaylistEntity>

}