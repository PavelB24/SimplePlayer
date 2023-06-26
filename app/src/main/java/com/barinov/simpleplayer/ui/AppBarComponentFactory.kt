package com.barinov.simpleplayer.ui

import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.barinov.simpleplayer.R

@Stable
class AppBarComponentFactory {


    fun appBarComponent(
        screen: Screen.ScreenRegister,
        Fab: @Composable () -> Unit,
        colorScaffoldBackground: Color,
        TopBarExtras: @Composable () -> Unit = {}
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
                    TopAppBar() {
                        TopBarExtras()
                    }
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

    @Composable
    private fun GetMenuInstance(
        screen: Screen.ScreenRegister,
        navState: NavHostController
    ) {
        when (screen) {
            Screen.ScreenRegister.HOME -> CreateDropDownMenu(screen, navState)
            Screen.ScreenRegister.IMPORT -> {
//                CreateDropDownMenu(screen, navState)
            }

            Screen.ScreenRegister.PLAYLISTS -> CreateActionIcon(navState)
            Screen.ScreenRegister.CURRENT_PLAY_LIST -> {
                //                CreateDropDownMenu(currScreen, navState)
            }

        }
    }


    @Composable
    private fun CreateDropDownMenu(
        screen: Screen.ScreenRegister,
        navState: NavHostController
    ) {
        val refs =
            when (screen) {
                Screen.ScreenRegister.PLAYLISTS -> arrayOf(R.string.load_tracks_menu_item)
                Screen.ScreenRegister.HOME -> {
                    arrayOf(R.string.about_menu_item, R.string.load_tracks_menu_item)
                }

                else -> {
                    arrayOf()
                }
            }
        val onClick: (Int) -> Unit =
            { ref ->
                when (ref) {
                    R.string.load_tracks_menu_item -> navState.navigate(Screen.ScreenRegister.IMPORT.name)
                    R.string.about_menu_item -> {}
                }
            }

        ExpandedMenu(refs = refs, onClick = onClick)
    }


    @Composable
    private fun CreateActionIcon(
        navState: NavHostController
    ) {
        MenuImageButton(Icons.Default.Add) {
            navState.navigate(Screen.ScreenRegister.IMPORT.name)
        }
    }

}