package com.barinov.simpleplayer.ui

import com.barinov.simpleplayer.domain.MassStorageProvider
import com.barinov.simpleplayer.domain.RootType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

sealed interface MenuType {


    data class FileBrowserType(
       val startRt: RootType,
//       val pathFlow: Flow<String>,
       val typeFlow: StateFlow<RootType>,
       val usbAccessFlow: StateFlow<MassStorageProvider.MassStorageState>
    ): MenuType

    object ScanScreenType: MenuType
}