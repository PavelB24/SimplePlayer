package com.barinov.simpleplayer.ui

import androidx.compose.animation.VectorConverter
import androidx.compose.foundation.layout.height
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.barinov.simpleplayer.toSystemColorsContainer

@Stable
class ScreenProvider {

    private val _currentScreen = mutableStateOf<Screen>(Screen.Home(
        backgroundContainer = ColorsProvider.obtainOnHomeScreen().toSystemColorsContainer()
    ))
    val currentScreen: State<Screen> = _currentScreen





    fun onScreenEnter(
        screen: Screen.ScreenRegister,
        Fab: @Composable () -> Unit = {},
        colors: SystemColorsContainer,
        TopNavigationIcon: @Composable () -> Unit = {},
        TopBarExtras: @Composable () -> Unit = {},
    ) {
        _currentScreen.value = when (screen) {
            Screen.ScreenRegister.HOME -> Screen.Home(
                backgroundContainer = colors
            )
            Screen.ScreenRegister.IMPORT -> Screen.Import(
                AppBarComponentFactory.obtainAppBarComponent(
                    TopBarExtras = TopBarExtras
                ),
                backgroundContainer = colors
            )

            Screen.ScreenRegister.PLAYLISTS -> Screen.Playlists(
                AppBarComponentFactory.obtainAppBarComponent(
                    TopBarExtras = TopBarExtras
                ),
                backgroundContainer = colors
            )

            Screen.ScreenRegister.SELECTED_PLAY_LIST -> Screen.TrackDetails(
                AppBarComponentFactory.obtainAppBarComponent(
                    TopBarExtras = TopBarExtras
                ),
                backgroundContainer = colors
            )

            Screen.ScreenRegister.TRACK_DETAILS -> TODO()
        }
    }


    @Stable
    private object AppBarComponentFactory {

        fun obtainAppBarComponent(
            topBarColor: Color = primary_color,
            TopBarExtras: @Composable () -> Unit = {},
            TopNavigationIcon: @Composable () -> Unit = {}
        ): @Composable () -> Unit {
            return {
                TopAppBar(
                    navigationIcon = { TopNavigationIcon() },
                    modifier = Modifier.height(48.dp),
                    title = { },
                    elevation = 0.dp,
                    contentColor = Color.Unspecified,
                    backgroundColor = topBarColor,
                    actions = {
                        TopBarExtras()
                    }
                )
            }

        }
    }
}
