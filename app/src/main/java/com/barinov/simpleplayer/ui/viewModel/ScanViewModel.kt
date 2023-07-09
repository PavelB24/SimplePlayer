package com.barinov.simpleplayer.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barinov.simpleplayer.domain.FileWorker
import com.barinov.simpleplayer.domain.MusicRepository
import com.barinov.simpleplayer.domain.util.SearchUtil
import com.barinov.simpleplayer.toCommonFileItem
import com.barinov.simpleplayer.ui.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ScanViewModel(
    private val searchUtil: SearchUtil,
    private val repository: MusicRepository,
): ViewModel() {



    val events: SharedFlow<FileWorker.FileEvents> = searchUtil.filesEventFlow

    val startScreenFlow: StateFlow<Screen.ScreenRegister> =  MutableStateFlow(Screen.ScreenRegister.HOME).also {
        viewModelScope.launch {
            if(repository.getTracksCount() > 0){
                Screen.ScreenRegister.PLAYLISTS
            } else Screen.ScreenRegister.HOME
        }
    }

    fun onComplete(){}

    fun getDefaultRoot() = searchUtil.defaultInternalFolder.toCommonFileItem()

}