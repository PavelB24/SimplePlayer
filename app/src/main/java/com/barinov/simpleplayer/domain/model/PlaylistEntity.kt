package com.barinov.simpleplayer.domain.model

import androidx.room.Entity

@Entity(tableName = "playlists")
data class PlaylistEntity(
    val id: String,
    val name: String
)