package com.barinov.simpleplayer.domain.model

import java.io.File

data class MusicFile(
    val id: String,
    val playlistId: String,
    val file: File
)
