package com.barinov.simpleplayer.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.barinov.simpleplayer.ui.viewModel.FileBrowserViewModel
import com.barinov.simpleplayer.base.ItemInteractor
import com.barinov.simpleplayer.domain.model.CommonFileItem
import com.barinov.simpleplayer.isFile
import com.barinov.simpleplayer.toSystemColorsContainer
import com.barinov.simpleplayer.ui.MenuFactory.getMenuInstance
import org.koin.androidx.compose.getViewModel

@Composable
fun FileBrowser(
    paddings: Pair<Dp, Dp>,
    navController: NavHostController,
    menuProvider: ScreenProvider,
    viewModel: FileBrowserViewModel = getViewModel()
) {
    val dark = isSystemInDarkTheme()
    val colors = if(!dark) ColorsProvider.obtainOnFileBrowserLight() else ColorsProvider.obtainDefaultLight()
    LaunchedEffect(key1 = Unit) {
        menuProvider.onScreenEnter(
            Screen.ScreenRegister.IMPORT,
            TopBarExtras = getMenuInstance(object : TopBarConnector.FileBrowserTopBarConnector() {
                override fun onFolderPeeked() {
//                    viewModel.importFromCurrentFolder()
                    viewModel.autoSearch()
                }
            }),
            colors = colors.toSystemColorsContainer()
        )
    }
    val files by viewModel.filesFlow.collectAsState()
    val backEnabler = remember {
        mutableStateOf(false)
    }
    BackHandler(backEnabler.value) {
        if (!viewModel.isBackStackGoingToEmpty()) {
            viewModel.goBack()
        } else {
            viewModel.goBack()
            backEnabler.value = false
        }
    }
    val interactor = remember {
        object : ItemInteractor<CommonFileItem> {
            override fun onClick(item: CommonFileItem) {
                if (!item.isFile()) {
                    viewModel.onFolderClicked(item)
                    if (!backEnabler.value) {
                        backEnabler.value = true
                    }
                }
            }

            override fun onLongClick(item: CommonFileItem): Boolean {
                return true
            }

        }
    }
    val lazyListState: LazyListState = rememberLazyListState()
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(top= 8.dp),
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(
                items = files,
                key = {it.signature},
                itemContent = {
                    FileItem(item = it, interactor = interactor)
                }
            )
        }
    }
}

@Composable
fun HomeScreen(
    menuProvider: ScreenProvider,
    navController: NavHostController,
) {
    val isDark = isSystemInDarkTheme()
    val colors = ColorsProvider.obtainOnHomeScreen()
    LaunchedEffect(key1 = Unit) {
        if (menuProvider.currentScreen.value !is Screen.Home) {
            menuProvider.onScreenEnter(
                Screen.ScreenRegister.HOME,
                colors = colors.toSystemColorsContainer()
            )
        }
    }
    Box(
        modifier = Modifier.background(colors.uiGradient!!).fillMaxSize()
    ) {
        WavesAnimatedHome {
            navController.navigate(Screen.ScreenRegister.IMPORT.name)
        }
    }
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Image(
//            painter = painterResource(id = R.drawable.play_icon),
//            contentDescription = null,
//            modifier = Modifier
//                .clickable(
//                    remember { MutableInteractionSource() },
//                    selectableRipple()
//                ) {
//                    navController.navigate(Screen.ScreenRegister.IMPORT.name)
//                }
//                .size(animation.dp)
//        )
//    }
}


@Composable
fun Playlists() {
}

@Composable
fun CurrentPlayList() {
}

@Composable
fun CurrentTrack() {
}