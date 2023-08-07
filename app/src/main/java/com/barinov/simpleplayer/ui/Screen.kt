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
        override val backgroundContainer: SystemColorsContainer
    ) : Screen(Toolbar, ScreenRegister.HOME, backgroundContainer)

    @Immutable
    data class Import(
        override val Toolbar: @Composable () -> Unit,
        override val backgroundContainer: SystemColorsContainer
    ) : Screen(Toolbar, ScreenRegister.IMPORT, backgroundContainer)

    @Immutable
    data class Playlists(
        override val Toolbar: @Composable () -> Unit,
        override val backgroundContainer: SystemColorsContainer
    ) : Screen(Toolbar, ScreenRegister.PLAYLISTS, backgroundContainer)

    @Immutable
    data class SelectedPlayList(
        override val Toolbar: @Composable () -> Unit,
        override val backgroundContainer: SystemColorsContainer
    ) : Screen(Toolbar, ScreenRegister.PLAYLISTS, backgroundContainer)

    @Immutable
    data class Scan(
        override val Toolbar: @Composable () -> Unit,
        override val backgroundContainer: SystemColorsContainer
    ) : Screen(Toolbar, ScreenRegister.IMPORT, backgroundContainer)

    @Immutable
    data class TrackDetails(
        override val Toolbar: @Composable () -> Unit,
        override val backgroundContainer: SystemColorsContainer
    ) : Screen(Toolbar, ScreenRegister.TRACK_DETAILS, backgroundContainer)


    @Immutable
    enum class ScreenRegister {
        HOME, IMPORT, PLAYLISTS, TRACK_DETAILS, SELECTED_PLAY_LIST, SCAN
    }
}