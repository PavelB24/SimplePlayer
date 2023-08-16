package com.barinov.simpleplayer.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.barinov.simpleplayer.ui.theme.primary_color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
@Composable
fun PlayerTheme() {
    val provider = staticCompositionLocalOf<ScreenProvider> { error("NotProvided") }

    val currentScreen = remember {
        ScreenProvider()
    }
    CompositionLocalProvider(provider.provides(currentScreen)) {
        rememberSystemUiController().also {
            currentScreen.currentScreen.value.backgroundContainer.apply {
                it.setStatusBarColor( systemTopUiColor)
                if(navBarColor != null) {
                    it.setNavigationBarColor(navBarColor)
                }
            }
        }

        MaterialTheme(
            colors = colors
        ) {
            Host(provider)
        }

    }

}


@Composable
fun Host(provider: ProvidableCompositionLocal<ScreenProvider>) {
    val scaffoldState = rememberScaffoldState()
    val navState = rememberNavController()
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {SnackbarHost(hostState = snackBarHostState)},
        topBar = {
            AnimatedVisibility(
                visible = checkTopBarVisibility(provider.current.currentScreen.value),
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                provider.current.currentScreen.value.Toolbar()
            }
//                    TopAppBar(
//                        navigationIcon = {},
//                        modifier = Modifier.height(58.dp),
//                        title = { },
//                        backgroundColor = primaryColor,
//                        actions = {
//                            appBarComponentFactory.GetMenuInstance(provider.current, navState)
//                        }
//
//                    )
        },
//        backgroundColor = Color(0xDDE6E0E2),
//        backgroundColor = provider.current.currentScreen.value.backgroundContainer.gradient,
        content = {
            NavigationHost(
                snackBarHostState,
                navState,
                Pair(it.calculateBottomPadding(), it.calculateTopPadding()),
                provider.current
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = provider.current.currentScreen.value is Screen.TrackDetails || provider.current.currentScreen.value is Screen.Playlists,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                BottomAppBar(
//                            modifier = Modifier.nestedScroll(),
                    backgroundColor = primary_color
                ) {

                }
            }

        }
    )
}

private fun checkTopBarVisibility(value: Screen): Boolean {
    return value is Screen.Import || value is Screen.Scan
}
