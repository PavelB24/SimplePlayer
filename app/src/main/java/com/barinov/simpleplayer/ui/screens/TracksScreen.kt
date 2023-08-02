package com.barinov.simpleplayer.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.navArgument
import com.barinov.simpleplayer.ui.TracksScreenState
import com.barinov.simpleplayer.ui.components.TrackPlayInfo
import com.barinov.simpleplayer.ui.viewModels.TracksViewModel
import org.koin.androidx.compose.getViewModel




@Composable
fun TracksScreen(
    playListId: String?,
    viewModel: TracksViewModel = getViewModel()
) {


    val state = viewModel.screenState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {

//        LazyColumn(content =)
        AnimatedVisibility(visible = shouldShowPlayBar(state.value)) {
            TrackPlayInfo()
        }
    }
}


private fun shouldShowPlayBar(state: TracksScreenState): Boolean {
    return state is TracksScreenState.Playing || state is TracksScreenState.Paused
}