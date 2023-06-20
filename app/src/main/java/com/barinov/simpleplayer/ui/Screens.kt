package com.barinov.simpleplayer.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun FileBrowser(){
    val context = LocalContext.current
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