package com.barinov.simpleplayer.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.barinov.simpleplayer.domain.model.TrackEntity
import com.barinov.simpleplayer.domain.model.PlaylistEntity

@Database(
    version = 1,
    entities = [TrackEntity::class, PlaylistEntity::class]
)
abstract class LocalDataBase: RoomDatabase() {

    abstract fun getTracksDao(): MusicTracksDao
}