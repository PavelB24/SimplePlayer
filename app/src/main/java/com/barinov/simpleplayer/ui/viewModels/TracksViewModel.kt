package com.barinov.simpleplayer.ui.viewModels

import android.media.MediaDrm.PlaybackComponent
import androidx.lifecycle.ViewModel
import com.barinov.simpleplayer.ui.TracksScreenState
import kotlinx.coroutines.flow.MutableStateFlow

class TracksViewModel: ViewModel() {


    val screenState: MutableStateFlow<TracksScreenState> = MutableStateFlow(TracksScreenState.Idle)
}