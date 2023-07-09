package com.barinov.simpleplayer.ui.viewModel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barinov.simpleplayer.domain.EventProvider
import com.barinov.simpleplayer.domain.MusicRepository
import com.barinov.simpleplayer.ui.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


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