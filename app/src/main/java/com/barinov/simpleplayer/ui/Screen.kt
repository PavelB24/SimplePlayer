package com.barinov.simpleplayer.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable


sealed class Screen(
    open val Toolbar: @Composable () -> Unit,
    open val screenName: ScreenRegister
) {
    @Immutable
    data class Home(
        override val Toolbar: @Composable () -> Unit = {},
        override val screenName: ScreenRegister = ScreenRegister.HOME
    ) : Screen(Toolbar,  screenName) {


    }

    @Immutable
    data class Import(
        override val Toolbar: @Composable () -> Unit,
        override val screenName: ScreenRegister = ScreenRegister.IMPORT
    ) : Screen(Toolbar, screenName)

    @Immutable
    data class Playlists(
        override val Toolbar: @Composable () -> Unit,
        override val screenName: ScreenRegister = ScreenRegister.PLAYLISTS
    ) : Screen(Toolbar, screenName)

    @Immutable
    data class CurrentPlay(
        override val Toolbar: @Composable () -> Unit,
        override val screenName: ScreenRegister = ScreenRegister.CURRENT_PLAY_LIST
    ) : Screen(Toolbar, screenName)



    @Immutable
    enum class ScreenRegister {
        HOME, IMPORT, PLAYLISTS, CURRENT_PLAY_LIST
    }
}