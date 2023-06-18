package com.barinov.simpleplayer.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.barinov.simpleplayer.domain.MusicStorageType

@Entity(tableName = "tracks")
@TypeConverters()
data class MusicFileMetadataEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    @ColumnInfo(name = "play_list_id")
    val playlistId: String,
    val path: String,
    val type: MusicStorageType,
    val album: String?,
    val artist: String?,
    val bitrate: String?,
    val duration: Long,
    val genre: String?,
    val date: String?
)