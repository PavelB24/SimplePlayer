package com.barinov.simpleplayer.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color


sealed class Screen(
    open val Toolbar: @Composable () -> Unit,
    open val Fab: @Composable () -> Unit,
    open val colorScaffoldBackground: Color,
    open val screenName: ScreenRegister
) {
    @Immutable
    data class Home(
        override val Toolbar: @Composable () -> Unit = {},
        override val Fab: @Composable () -> Unit,
        override val colorScaffoldBackground: Color,
        override val screenName: ScreenRegister = ScreenRegister.HOME
    ) : Screen(Toolbar, Fab, colorScaffoldBackground, screenName) {


    }

    @Immutable
    data class Import(
        override val Toolbar: @Composable () -> Unit,
        override val Fab: @Composable () -> Unit,
        override val colorScaffoldBackground: Color,
        override val screenName: ScreenRegister = ScreenRegister.IMPORT
    ) : Screen(Toolbar, Fab, colorScaffoldBackground, screenName)

    @Immutable
    data class Playlists(
        override val Toolbar: @Composable () -> Unit,
        override val Fab: @Composable () -> Unit,
        override val colorScaffoldBackground: Color,
        override val screenName: ScreenRegister = ScreenRegister.PLAYLISTS
    ) : Screen(Toolbar, Fab, colorScaffoldBackground, screenName)

    @Immutable
    data class CurrentPlay(
        override val Toolbar: @Composable () -> Unit,
        override val Fab: @Composable () -> Unit,
        override val colorScaffoldBackground: Color,
        override val screenName: ScreenRegister = ScreenRegister.CURRENT_PLAY_LIST
    ) : Screen(Toolbar, Fab, colorScaffoldBackground, screenName)



    @Immutable
    enum class ScreenRegister {
        HOME, IMPORT, PLAYLISTS, CURRENT_PLAY_LIST
    }
}