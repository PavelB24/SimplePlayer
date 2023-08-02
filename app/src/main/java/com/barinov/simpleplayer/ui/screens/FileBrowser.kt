package com.barinov.simpleplayer.ui.screens

import android.bluetooth.BluetoothManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.barinov.simpleplayer.base.ItemInteractor
import com.barinov.simpleplayer.domain.model.CommonFileItem
import com.barinov.simpleplayer.isFile
import com.barinov.simpleplayer.toSystemColorsContainer
import com.barinov.simpleplayer.ui.MenuType
import com.barinov.simpleplayer.ui.ColorsProvider
import com.barinov.simpleplayer.ui.components.items.FileItem
import com.barinov.simpleplayer.ui.Screen
import com.barinov.simpleplayer.ui.ScreenProvider
import com.barinov.simpleplayer.ui.TopBarConnector
import com.barinov.simpleplayer.ui.components.TopBarBackButton
import com.barinov.simpleplayer.ui.menuFactory.ScreenMenuProvider
import com.barinov.simpleplayer.ui.viewModels.FileBrowserViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun FileBrowser(
    paddings: Pair<Dp, Dp>,
    navController: NavHostController,
    menuProvider: ScreenProvider,
    viewModel: FileBrowserViewModel = getViewModel()
) {
    LocalContext.current.getSystemService(BluetoothManager::class.java)
    val isSystemInDarkTheme = isSystemInDarkTheme()


    LaunchedEffect(key1 = Unit) {
        onScreenEnter(viewModel, menuProvider, navController, isSystemInDarkTheme)
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
    val interactor =
        @Immutable
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

    val lazyListState: LazyListState = rememberLazyListState()
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(top = 8.dp),
            state = lazyListState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = files,
                key = { it.signatureString },
                itemContent = {
                    FileItem(item = it, interactor = interactor)
                }
            )
        }
    }
}

private fun onScreenEnter(
    viewModel: FileBrowserViewModel,
    menuProvider: ScreenProvider,
    navController: NavHostController,
    dark: Boolean
) {

    val colors =
        if (!dark) ColorsProvider.obtainOnFileBrowserLight() else ColorsProvider.obtainDefaultLight()
    menuProvider.onScreenEnter(
        NavIcon = {
            TopBarBackButton {
                navController.navigateUp()
            }
        },
        screen = Screen.ScreenRegister.IMPORT,
        TopBarExtras = ScreenMenuProvider.provide(
            MenuType.FileBrowserType(
                viewModel.getRootType(),
                viewModel.getRtFlow(),
                viewModel.massStorageState
            )
        ).getInstance(
            @Immutable
            object : TopBarConnector.FileBrowserTopBarConnector() {
                override fun onFolderPeeked() {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(PATH_KEY, viewModel.peekFolder())
                    navController.popBackStack()
                }

                override fun changeRootType() {
                    viewModel.changeRootType()
                }
            }
        ),
        colors = colors.toSystemColorsContainer()
    )
}
