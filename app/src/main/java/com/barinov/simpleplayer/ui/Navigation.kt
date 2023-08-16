package com.barinov.simpleplayer.ui

import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.barinov.simpleplayer.ui.screens.FileBrowser
import com.barinov.simpleplayer.ui.screens.HomeScreen
import com.barinov.simpleplayer.ui.screens.ScanScreen
import com.barinov.simpleplayer.ui.screens.TracksScreen
import com.barinov.simpleplayer.ui.viewModels.ScanViewModel
import org.koin.androidx.compose.getViewModel


const val playListSelectedKey = "play_list_click"

@Composable
fun NavigationHost(
    snackBarState: SnackbarHostState,
    navController: NavHostController,
    paddings: Pair<Dp, Dp>,
    menuProvider: ScreenProvider,
) {

    val hostViewModel: ScanViewModel = getViewModel()
//    val darkTheme = isSystemInDarkTheme()
    val startScreenState = hostViewModel.startScreenFlow.collectAsState()

    NavHost(
        navController = navController,
//        startDestination = startScreenState.value.name
        startDestination = Screen.ScreenRegister.HOME.name
    )
    {
        composable(Screen.ScreenRegister.IMPORT.name) {
            FileBrowser(
                paddings = paddings,
                navController = navController,
                menuProvider = menuProvider
            )
        }

        composable(Screen.ScreenRegister.SCAN.name){
            ScanScreen(
                snackBarState,
                navController = navController,
                menuProvider = menuProvider
            ,)
        }

        composable(Screen.ScreenRegister.HOME.name){
            HomeScreen(menuProvider, navController)
        }

        composable(Screen.ScreenRegister.PLAYLISTS.name){
            TracksScreen(it.arguments?.getString(playListSelectedKey))
        }


//        composable(Screen.SETTINGS.name) {
//            SettingsScreen()
//        }
    }
}