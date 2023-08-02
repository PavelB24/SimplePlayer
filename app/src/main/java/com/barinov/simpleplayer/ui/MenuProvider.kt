package com.barinov.simpleplayer.ui

import androidx.compose.runtime.Composable

interface MenuProvider {

    fun getInstance(
        connector: TopBarConnector?
    ): @Composable () -> Unit
}