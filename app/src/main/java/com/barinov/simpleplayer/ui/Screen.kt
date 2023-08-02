package com.barinov.simpleplayer.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable


sealed class Screen(
    open val Toolbar: @Composable () -> Unit,
    open val screenName: ScreenRegister,
    open val backgroundContainer: SystemColorsContainer
) {
    @Immutable
    data class Home(
        override val Toolbar: @Composable () -> Unit = {},
        override val screenName: ScreenRegister = ScreenRegister.HOME,
        override val backgroundContainer: SystemColorsContainer
    ) : Screen(Toolbar,  screenName, backgroundContainer)

    @Immutable
    data class Import(
        override val Toolbar: @Composable () -> Unit,
        override val screenName: ScreenRegister = ScreenRegister.IMPORT,
        override val backgroundContainer: SystemColorsContainer
    ) : Screen(Toolbar, screenName, backgroundContainer)

    @Immutable
    data class Playlists(
        override val Toolbar: @Composable () -> Unit,
        override val screenName: ScreenRegister = ScreenRegister.PLAYLISTS,
        override val backgroundContainer: SystemColorsContainer
    ) : Screen(Toolbar, screenName, backgroundContainer)

    @Immutable
    data class SelectedPlayList(
        override val Toolbar: @Composable () -> Unit,
        override val screenName: ScreenRegister = ScreenRegister.PLAYLISTS,
        override val backgroundContainer: SystemColorsContainer
    ) :  Screen(Toolbar, screenName, backgroundContainer)

    @Immutable
    data class TrackDetails(
        override val Toolbar: @Composable () -> Unit,
        override val screenName: ScreenRegister = ScreenRegister.TRACK_DETAILS,
        override val backgroundContainer: SystemColorsContainer
    ) : Screen(Toolbar, screenName, backgroundContainer)



    @Immutable
    enum class ScreenRegister {
        HOME, IMPORT, PLAYLISTS, TRACK_DETAILS, SELECTED_PLAY_LIST, SCAN
    }
}