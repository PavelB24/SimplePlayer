package com.barinov.simpleplayer.ui

import android.util.Log
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

@Stable
class ScreenProvider {

    private val _currentScreen = mutableStateOf<Screen>(Screen.Home())
    val currentScreen: State<Screen> = _currentScreen

    @Stable
    private val appBarHelperFactory = AppBarComponentFactory()

    fun setScreen(
        screen: Screen.ScreenRegister,
        TopBarExtras: @Composable () -> Unit = {},
        Fab: @Composable () -> Unit,
        colorScaffoldBackground: Color,
    ){
        _currentScreen.value = when(screen){
            Screen.ScreenRegister.HOME -> Screen.Home()
            Screen.ScreenRegister.IMPORT -> Screen.Import(appBarHelperFactory.appBarComponent(screen = screen, Fab, colorScaffoldBackground,  TopBarExtras))
            Screen.ScreenRegister.PLAYLISTS -> Screen.Playlists(appBarHelperFactory.appBarComponent(screen = screen, Fab, colorScaffoldBackground,  TopBarExtras))
            Screen.ScreenRegister.CURRENT_PLAY_LIST -> Screen.CurrentPlay(appBarHelperFactory.appBarComponent(screen = screen, Fab, colorScaffoldBackground,  TopBarExtras))
        }
    }
}