package com.barinov.simpleplayer.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.barinov.simpleplayer.ui.viewModel.FileBrowserViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun FileBrowser(){
    val context = LocalContext.current
    val viewModel: FileBrowserViewModel = koinViewModel()
    LazyColumn{
        items(
            items = listOf(),
            itemContent = {
                FileItem()
            }
        )
    }
}

@Composable
fun MainScreen(){}