package com.barinov.simpleplayer.ui

import androidx.compose.runtime.Composable
import com.barinov.simpleplayer.domain.RootType

interface MenuFactoryI {

    fun getMenuInstance(
        connector: TopBarConnector,
    ): @Composable () -> Unit
}