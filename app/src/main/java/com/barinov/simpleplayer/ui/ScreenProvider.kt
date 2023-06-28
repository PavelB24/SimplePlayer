package com.barinov.simpleplayer.ui

import androidx.compose.foundation.layout.height
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Stable
class ScreenProvider {

    private val _currentScreen = mutableStateOf<Screen>(Screen.Home())
    val currentScreen: State<Screen> = _currentScreen


    fun onScreenEnter(
        screen: Screen.ScreenRegister,
        Fab: @Composable () -> Unit = {},
        scaffoldBackgroundColor: Color = primaryColor,
        TopNavigationIcon: @Composable () -> Unit = {},
        TopBarExtras: @Composable () -> Unit = {},
    ) {
        _currentScreen.value = when (screen) {
            Screen.ScreenRegister.HOME -> Screen.Home()
            Screen.ScreenRegister.IMPORT -> Screen.Import(
                AppBarComponentFactory.obtainAppBarComponent(
                    TopBarExtras = TopBarExtras
                )
            )

            Screen.ScreenRegister.PLAYLISTS -> Screen.Playlists(
                AppBarComponentFactory.obtainAppBarComponent(
                    TopBarExtras = TopBarExtras
                )
            )

            Screen.ScreenRegister.SELECTED_PLAY_LIST -> Screen.TrackDetails(
                AppBarComponentFactory.obtainAppBarComponent(
                    TopBarExtras = TopBarExtras
                )
            )

            Screen.ScreenRegister.TRACK_DETAILS -> TODO()
        }
    }


    @Stable
    private object AppBarComponentFactory {

        fun obtainAppBarComponent(
            topBarColor: Color = primaryColor,
            TopBarExtras: @Composable () -> Unit = {},
            TopNavigationIcon: @Composable () -> Unit = {}
        ): @Composable () -> Unit {
            return {
                TopAppBar(
                    navigationIcon = { TopNavigationIcon() },
                    modifier = Modifier.height(58.dp),
                    title = { },
                    elevation = 1.dp,
                    backgroundColor = topBarColor,
                    actions = {
                        TopBarExtras()
                    }
                )
            }

        }
    }
}
