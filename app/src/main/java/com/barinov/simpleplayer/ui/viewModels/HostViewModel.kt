package com.barinov.simpleplayer.ui.viewModels

import androidx.lifecycle.ViewModel
import com.barinov.simpleplayer.domain.EventProvider


class HostViewModel(

    private val eventsProvider: EventProvider
) : ViewModel() {







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