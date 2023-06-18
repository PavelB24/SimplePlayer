package com.barinov.simpleplayer.domain.model

data class MusicFileMetaData(
    val title: String,
    val cover: ByteArray?,
    val album: String?,
    val artist: String?,
    val bitrate: String?,
    val duration: Long?,
    val genre: String?,
    val date: String?
)
