package com.barinov.simpleplayer.domain.model

import com.barinov.simpleplayer.domain.RootType
import me.jahnen.libaums.core.fs.FileSystem
import me.jahnen.libaums.core.fs.UsbFile
import java.io.File
import java.util.UUID

data class CommonFileItem(
    val len: Long,
    val rootType: RootType,
    val iFile: File?,
    val uEntity: UsbData?,
    val id: UUID = UUID.randomUUID(),
){
    data class UsbData(
        val uFile: UsbFile,
        val fs: FileSystem

    )

}