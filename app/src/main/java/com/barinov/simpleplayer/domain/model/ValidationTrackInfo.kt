package com.barinov.simpleplayer.domain.model

import androidx.room.ColumnInfo

data class ValidationTrackInfo(
    val id: String,
    val path: String,
    @ColumnInfo(name = "play_list_id")
    val playListId: String

)
