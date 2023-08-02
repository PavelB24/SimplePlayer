package com.barinov.simpleplayer.ui.menuFactory


import androidx.compose.runtime.Composable
import com.barinov.simpleplayer.ui.MenuProvider
import com.barinov.simpleplayer.ui.TopBarConnector

class ScanScreenMenuProvider() : MenuProvider {


    override fun getInstance(
        connector: TopBarConnector?
    ): @Composable () -> Unit {
        when (connector) {
            null -> {
                return {}
            }
            else -> {
                throw IllegalArgumentException("${this::class.java.name} incomparable with this TopBarConnector")
            }
        }
    }
}