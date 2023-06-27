package com.barinov.simpleplayer.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.navigation.NavHostController
import com.barinov.simpleplayer.R

@Stable
object MenuFactory {

    @Composable
    private fun GetMenuInstance(
        screen: Screen.ScreenRegister,
        navState: NavHostController,
        actionList: TopBarActions
    ) {
        when (screen) {
            Screen.ScreenRegister.HOME -> CreateDropDownMenu(screen, navState)
            Screen.ScreenRegister.IMPORT -> {
                CreateActionIcon(navState = navState, actionList)
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
        navState: NavHostController,
        actionList: TopBarActions
    ) {
        if(actionList !is TopBarActions.FileBrowserTopBarActions) throw  IllegalArgumentException()
        MenuImageButton(Icons.Default.Add) {
            navState.navigate(Screen.ScreenRegister.IMPORT.name)
        }
    }
}