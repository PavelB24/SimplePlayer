package com.barinov.simpleplayer.ui

import androidx.compose.foundation.layout.height
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.barinov.simpleplayer.R

@Stable
object AppBarComponentFactory {


    fun appBarComponent(
        screen: Screen.ScreenRegister,
        toolbarColor: Color,
        TopBarExtras: @Composable () -> Unit = {},
        TopNavigationIcon: @Composable () -> Unit = {}
    ): @Composable () -> Unit {
        return when (screen) {
            Screen.ScreenRegister.HOME -> {
                @Composable {
//                    TopAppBar() {
//
//                    }
                }
            }

            Screen.ScreenRegister.IMPORT -> {
                @Composable {
                    TopAppBar(
                        navigationIcon = {TopNavigationIcon()},
                        modifier = Modifier.height(58.dp),
                        title = { },
                        backgroundColor = toolbarColor,
                        actions = {
                            TopBarExtras()
                        }
                    )
                }
            }
            Screen.ScreenRegister.PLAYLISTS -> {
                @Composable {
                    TopAppBar() {
                        TopBarExtras()
                    }
                }
            }
            Screen.ScreenRegister.CURRENT_PLAY_LIST -> {
                @Composable {
                    TopAppBar() {
                        TopBarExtras()
                    }
                }
            }

        }
    }

}