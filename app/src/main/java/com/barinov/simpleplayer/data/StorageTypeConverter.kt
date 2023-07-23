package com.barinov.simpleplayer.data

import androidx.room.TypeConverter
import com.barinov.simpleplayer.domain.MusicStorageType


class StorageTypeConverter {

    @TypeConverter
    fun toType(ordinal: Int) = MusicStorageType.values()[ordinal]

    @TypeConverter
    fun fromType(type: MusicStorageType) = type.ordinal


}