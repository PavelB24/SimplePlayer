package com.barinov.simpleplayer.ui.viewModel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barinov.simpleplayer.domain.EventProvider
import com.barinov.simpleplayer.domain.FileWorker
import com.barinov.simpleplayer.domain.MusicRepository
import com.barinov.simpleplayer.ui.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Immutable
class HostViewModel(
    private val repository: MusicRepository,
    private val searchEvents: EventProvider
) : ViewModel() {



    val startScreenFlow: MutableStateFlow<Screen.ScreenRegister> =
        MutableStateFlow(
            if (repository.getTracksCount() == 0)
                Screen.ScreenRegister.HOME
            else Screen.ScreenRegister.PLAYLISTS
        )

    init {
        viewModelScope.launch {
            searchEvents.filesEventFlow.onEach {
                if(it is FileWorker.FileEvents.OnSearchCompleted){
                    startScreenFlow.emit(Screen.ScreenRegister.PLAYLISTS)
                }
            }.collect()
        }
    }

}