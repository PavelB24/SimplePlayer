package com.barinov.simpleplayer.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.barinov.simpleplayer.ui.viewModel.HostViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun NavigationHost(
    navController: NavHostController,
    paddings: Pair<Dp, Dp>,
    menuProvider: ScreenProvider,
) {

    val hostViewModel: HostViewModel = getViewModel()
    val startScreenState = hostViewModel.startScreenFlow.collectAsState()

    NavHost(
        navController = navController,
        startDestination = startScreenState.value.screenName.name
    )
    {
        composable(Screen.ScreenRegister.IMPORT.name) {
            FileBrowser(paddings = paddings , navController = navController, menuProvider = menuProvider)
        }

        composable(Screen.ScreenRegister.HOME.name){
            HomeScreen(menuProvider)
        }

//        composable(Screen.SETTINGS.name) {
//            SettingsScreen()
//        }
    }
}