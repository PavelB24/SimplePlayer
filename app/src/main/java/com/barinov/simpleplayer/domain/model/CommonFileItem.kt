package com.barinov.simpleplayer.domain.model

import com.barinov.simpleplayer.domain.CoroutineFileWorker
import me.jahnen.libaums.core.fs.FileSystem
import me.jahnen.libaums.core.fs.UsbFile
import java.io.File

data class CommonFileItem(
    val rootType: CoroutineFileWorker.RootType,
    val iFile: File?,
    val uEntity: UsbData?
){
    data class UsbData(
        val uFile: UsbFile,
        val fs: FileSystem

    )

}