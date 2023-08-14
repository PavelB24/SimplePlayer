package com.barinov.simpleplayer.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


class HostViewModel() : ViewModel() {

    private val _askPermissionFlow = MutableSharedFlow<Unit>()
    val askPermissionFlow = _askPermissionFlow.asSharedFlow()


    fun askPermission(){
        viewModelScope.launch(Dispatchers.IO) {
            _askPermissionFlow.emit(Unit)
        }
    }



//    init {
//        viewModelScope.launch {
//            searchEvents.filesEventFlow.onEach {
//                if(it is FileWorker.FileEvents.OnSearchCompleted){
//                    startScreenFlow.emit(Screen.ScreenRegister.PLAYLISTS)
//                }
//            }.collect()
//        }
//    }

}