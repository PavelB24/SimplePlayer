package com.barinov.simpleplayer.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barinov.simpleplayer.domain.FileWorker
import com.barinov.simpleplayer.domain.MusicRepository
import com.barinov.simpleplayer.domain.model.CommonFileItem
import com.barinov.simpleplayer.domain.util.SearchUtil
import com.barinov.simpleplayer.toCommonFileItem
import com.barinov.simpleplayer.ui.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ScanViewModel(
    private val searchUtil: SearchUtil,
    private val repository: MusicRepository,
) : ViewModel() {


    val events: SharedFlow<FileWorker.FileWorkEvents> = searchUtil.filesEventFlow

    val startScreenFlow: MutableStateFlow<Screen.ScreenRegister> =
        MutableStateFlow(Screen.ScreenRegister.HOME).also {
            viewModelScope.launch(Dispatchers.IO) {
                if (repository.getTracksCount() > 0) {
                   it.emit(Screen.ScreenRegister.PLAYLISTS)
                } else  it.emit(Screen.ScreenRegister.HOME)
            }
        }

    fun directPutSearchResults(musicItemUuid: UUID, isSelected: Boolean) =
        searchUtil.directPutSearchResults(musicItemUuid, isSelected)


    fun confirm(
        copy: Boolean,
        plName: String
    ){
        searchUtil.confirmAndHandle(plName, copy){
            viewModelScope.launch(Dispatchers.IO) {
//                startScreenFlow.emit(Screen.ScreenRegister.PLAYLISTS)
            }
        }
    }

    fun startScan(
        playList: String,
        folder: CommonFileItem
    ){
        searchUtil.searchWithSubFolders(
            folder,
            playList.ifEmpty { null }
        )
    }

    fun getDefaultRoot() = searchUtil.defaultInternalFolder.toCommonFileItem()
    fun skipState() {
        searchUtil.cancelResults()
    }

}