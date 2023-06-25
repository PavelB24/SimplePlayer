package com.barinov.simpleplayer.domain

import kotlinx.coroutines.flow.SharedFlow
import me.jahnen.libaums.core.fs.FileSystem
import me.jahnen.libaums.core.fs.UsbFile

interface MassStorageProvider {


     fun openFolder(uFile: UsbFile?)

    val mssStorageDeviceAccessibilityFlow: SharedFlow<MassStorageState>



    sealed class MassStorageState(){

        object NotReady: MassStorageState()

        data class Ready(val uFiles: Pair<FileSystem, List<UsbFile>>): MassStorageState()
    }
}