package com.barinov.simpleplayer.domain

import kotlinx.coroutines.flow.StateFlow
import me.jahnen.libaums.core.fs.FileSystem
import me.jahnen.libaums.core.fs.UsbFile

interface MassStorageProvider {




    val mssStorageDeviceAccessibilityFlow: StateFlow<MassStorageState>

    fun openFolder(uFile: UsbFile?)

    fun getRoot(): Pair<FileSystem, UsbFile>
    sealed class MassStorageState() {

        object NotReady : MassStorageState()

        data class Ready(val uFiles: Pair<FileSystem, List<UsbFile>>) : MassStorageState()
    }
}