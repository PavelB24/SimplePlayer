package com.barinov.simpleplayer.domain.model

import com.barinov.simpleplayer.domain.MusicStorageType

data class MusicFileMetadataEntity(
    val id: String,
    val playlistId: String,
    val path: String,
    val type: MusicStorageType
)