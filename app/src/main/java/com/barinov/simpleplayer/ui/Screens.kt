package com.barinov.simpleplayer.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavHostController
import com.barinov.simpleplayer.ui.viewModel.FileBrowserViewModel
import com.barinov.simpleplayer.base.ItemInteractor
import com.barinov.simpleplayer.domain.model.CommonFileItem
import com.barinov.simpleplayer.isFile
import com.barinov.simpleplayer.ui.MenuFactory.getMenuInstance
import org.koin.androidx.compose.getViewModel

@Composable
fun FileBrowser(
    paddings: Pair<Dp, Dp>,
    navController: NavHostController,
    menuProvider: ScreenProvider,
    viewModel: FileBrowserViewModel = getViewModel()
) {
    LaunchedEffect(key1 = Unit) {
        menuProvider.onScreenEnter(
            Screen.ScreenRegister.IMPORT,
            TopBarExtras = getMenuInstance( object: TopBarConnector.FileBrowserTopBarConnector(){
                override fun onFolderPeeked() {
                    viewModel.importFromCurrentFolder()
                }
            })
        )
    }
    val files = viewModel.filesFlow.collectAsState()
    val backEnabler = remember {
        mutableStateOf(false)
    }
    BackHandler(backEnabler.value) {
        if (!viewModel.isBackStackGoingToEmpty()){
            viewModel.goBack()
        } else {
            viewModel.goBack()
            backEnabler.value = false
        }
    }
    val interactor = object : ItemInteractor<CommonFileItem> {
        override fun onClick(item: CommonFileItem) {
            if(!item.isFile()){
                viewModel.onFolderClicked(item)
                if(!backEnabler.value){
                    backEnabler.value = true
                }
            }
        }

        override fun onLongClick(item: CommonFileItem): Boolean {
            return true
        }

    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues()
        ) {
            items(
                items = files.value,
                itemContent = {
                    FileItem(item = it, interactor = interactor)
                }
            )
        }
    }
}

@Composable
fun HomeScreen(menuProvider: ScreenProvider) {
    menuProvider.onScreenEnter(Screen.ScreenRegister.HOME)
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