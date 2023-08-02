package com.barinov.simpleplayer.ui.viewModels

import androidx.lifecycle.ViewModel
import com.barinov.simpleplayer.domain.MusicRepository

class PlayListsViewModel(
    private val musicRepository: MusicRepository
): ViewModel() {



    val playLists = musicRepository.allPlayLists()



}