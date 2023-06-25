package com.barinov.simpleplayer.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.barinov.simpleplayer.R

@Stable
class AppBarComponentFactory {

    @Composable
    fun GetMenuInstance(
        screen: ScreenProvider,
        navState: NavHostController
    ) {
        val currScreen by screen.currentScreen
        when (currScreen) {
            Screen.HOME -> {
                CreateDropDownMenu(currScreen, navState)
            }
            Screen.IMPORT -> {
//                createDropDownMenu(currScreen, navState)
            }
            Screen.PLAYLISTS -> {
                CreateActionIcon(navState)
            }
            Screen.CURRENT_PLAY_LIST -> {
//                CreateDropDownMenu(currScreen, navState)
            }
        }
    }


    @Composable
    private fun CreateDropDownMenu(
        screen: Screen,
        navState: NavHostController
    ) {
        val refs =
            when (screen) {
                Screen.PLAYLISTS -> arrayOf(R.string.load_tracks_menu_item)
                Screen.HOME -> {
                    arrayOf(R.string.about_menu_item, R.string.load_tracks_menu_item)
                }
                else -> {
                    arrayOf()
                }
            }
        val onClick: (Int) -> Unit =
            { ref ->
                when (ref) {
                    R.string.load_tracks_menu_item -> navState.navigate(Screen.IMPORT.name)
                    R.string.about_menu_item -> {}
                }
            }

        ExpandedMenu(refs = refs, onClick = onClick)
    }


    @Composable
    private fun CreateActionIcon(
        navState: NavHostController
    ) {
        MenuImageButton(Icons.Default.Add){
            navState.navigate(Screen.IMPORT.name)
        }
    }

}