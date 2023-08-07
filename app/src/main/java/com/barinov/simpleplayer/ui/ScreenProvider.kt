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
import com.barinov.simpleplayer.ui.components.TopBarBackButton
import com.barinov.simpleplayer.ui.theme.primary_color
import com.barinov.simpleplayer.ui.theme.top_bar_color

@Stable
class ScreenProvider {

    private val _currentScreen = mutableStateOf<Screen>(
        Screen.Home(
            backgroundContainer = ColorsProvider.obtainOnHomeScreen().toSystemColorsContainer()
        )
    )
    val currentScreen: State<Screen> = _currentScreen


    fun onScreenEnter(
        screen: Screen.ScreenRegister,
        Fab: @Composable () -> Unit = {},
        NavIcon: @Composable () -> Unit = {},
        colors: SystemColorsContainer,
        TopBarExtras: @Composable () -> Unit = {},
    ) {
        _currentScreen.value = when (screen) {
            Screen.ScreenRegister.HOME -> Screen.Home(
                backgroundContainer = colors
            )

            Screen.ScreenRegister.IMPORT -> Screen.Import(
                AppBarComponentFactory.obtainAppBarComponent(
                    NavIcon = { NavIcon() },
                    TopBarExtras = TopBarExtras,
                    topBarColor = colors.systemTopUiColor
                ),
                backgroundContainer = colors
            )

            Screen.ScreenRegister.PLAYLISTS -> Screen.Playlists(
                AppBarComponentFactory.obtainAppBarComponent(
                    NavIcon = { NavIcon() },
                    TopBarExtras = TopBarExtras
                ),
                backgroundContainer = colors
            )

            Screen.ScreenRegister.SELECTED_PLAY_LIST -> Screen.TrackDetails(
                AppBarComponentFactory.obtainAppBarComponent(
                    NavIcon = { NavIcon() },
                    TopBarExtras = TopBarExtras
                ),
                backgroundContainer = colors
            )

            Screen.ScreenRegister.TRACK_DETAILS -> TODO()
            Screen.ScreenRegister.SCAN -> {
                Screen.Scan(
                    AppBarComponentFactory.obtainAppBarComponent(
                        NavIcon = { NavIcon() },
                        TopBarExtras = TopBarExtras,
                        topBarColor = colors.systemTopUiColor
                    ),
                    backgroundContainer = colors
                )
            }
        }
    }


    @Stable
    private object AppBarComponentFactory {

        fun obtainAppBarComponent(
            topBarColor: Color = primary_color,
            NavIcon: @Composable () -> Unit = {},
            TopBarExtras: @Composable () -> Unit = {},
        ): @Composable () -> Unit {
            return {
                TopAppBar(
                    navigationIcon = { NavIcon() },
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
