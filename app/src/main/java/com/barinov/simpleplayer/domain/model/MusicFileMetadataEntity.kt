package com.barinov.simpleplayer.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.TypeConverters
import com.barinov.simpleplayer.domain.MusicStorageType

@Entity(tableName = "tracks")
@TypeConverters()
data class MusicFileMetadataEntity(
    val id: String,
    @ColumnInfo(name = "play_list_id")
    val playlistId: String,
    val path: String,
    val type: MusicStorageType
)