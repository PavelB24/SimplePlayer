package com.barinov.simpleplayer.ui.viewModel

import androidx.lifecycle.ViewModel
import com.barinov.simpleplayer.domain.MusicRepository
import com.barinov.simpleplayer.ui.Screen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HostViewModel(
    private val repository: MusicRepository
): ViewModel() {

    val importedTracksCountFlow = repository.getTracksCountFlow()

    val startScreenFlow: StateFlow<Screen> = MutableStateFlow(Screen.HOME)
}