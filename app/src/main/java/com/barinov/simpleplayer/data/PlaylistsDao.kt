package com.barinov.simpleplayer.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistsDao {

    @Query("SELECT COUNT(*)")
    fun getClaimedPlaylistsFlowCount(): Flow<Int>
}