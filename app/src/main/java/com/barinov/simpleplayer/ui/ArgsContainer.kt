package com.barinov.simpleplayer.ui

import com.barinov.simpleplayer.domain.MassStorageProvider
import com.barinov.simpleplayer.domain.RootType
import kotlinx.coroutines.flow.StateFlow

sealed interface ArgsContainer {


    data class FileBrowserArgs(
       val startRt: RootType,
       val typeFlow: StateFlow<RootType>,
       val usbAccessFlow: StateFlow<MassStorageProvider.MassStorageState>
    ): ArgsContainer
}