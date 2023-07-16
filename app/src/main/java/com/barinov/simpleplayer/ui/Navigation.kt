package com.barinov.simpleplayer.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.barinov.simpleplayer.ui.screens.FileBrowser
import com.barinov.simpleplayer.ui.screens.HomeScreen
import com.barinov.simpleplayer.ui.viewModel.HostViewModel
import com.barinov.simpleplayer.ui.viewModel.ScanViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun NavigationHost(
    navController: NavHostController,
    paddings: Pair<Dp, Dp>,
    menuProvider: ScreenProvider,
) {

    val hostViewModel: ScanViewModel = getViewModel()
//    val darkTheme = isSystemInDarkTheme()
    val startScreenState = hostViewModel.startScreenFlow.collectAsState()

    NavHost(
        navController = navController,
        startDestination = startScreenState.value.name
    )
    {
        composable(Screen.ScreenRegister.IMPORT.name) {
            FileBrowser(
                paddings = paddings,
                navController = navController,
                menuProvider = menuProvider
            )
        }

        composable(Screen.ScreenRegister.HOME.name){
            HomeScreen(menuProvider, navController)
        }


//        composable(Screen.SETTINGS.name) {
//            SettingsScreen()
//        }
    }
}