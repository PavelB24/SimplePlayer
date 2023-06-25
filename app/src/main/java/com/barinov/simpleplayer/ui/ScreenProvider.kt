package com.barinov.simpleplayer.ui

import android.util.Log
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

@Stable
class ScreenProvider {

    private val _currentScreen = mutableStateOf(Screen.HOME)
    val currentScreen: State<Screen> = _currentScreen



    fun setScreen(screen: Screen){
        _currentScreen.value = screen
    }
}