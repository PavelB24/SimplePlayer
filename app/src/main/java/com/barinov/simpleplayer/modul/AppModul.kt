package com.barinov.simpleplayer.modul

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.barinov.simpleplayer.broadcastReceivers.UsbEventsBroadcastReceiver
import com.barinov.simpleplayer.core.MediaController
import com.barinov.simpleplayer.core.MediaEngine
import com.barinov.simpleplayer.data.LocalDataBase
import com.barinov.simpleplayer.domain.MassStorageProvider
import com.barinov.simpleplayer.domain.MusicRepository
import com.barinov.simpleplayer.domain.Player
import com.barinov.simpleplayer.prefs.PreferencesManager
import com.barinov.simpleplayer.ui.viewModel.FileBrowserViewModel
import com.barinov.simpleplayer.ui.viewModel.HostViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

const val SHARED_PREFS_NAME = "simple_player_prefs"
const val DATA_BASE_NAME = "simple_player_db"

val module = module {



    single {
        Room.databaseBuilder(
            androidApplication(),
            LocalDataBase::class.java,
            DATA_BASE_NAME
        ).build()
    }

    single {
        UsbEventsBroadcastReceiver()
    }

    single {
        MusicRepository(get<LocalDataBase>().getTracksDao())
    }

    single {
        MusicRepository(get<LocalDataBase>().getTracksDao())
    }

    single{
        MediaEngine(get())
    }

    single {
        PreferencesManager(androidApplication().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE))
    }

    single { holder->
        MediaController(androidApplication(), get<MediaEngine>() as Player, get(), holder.get())
    }

    viewModel {
        FileBrowserViewModel(get<UsbEventsBroadcastReceiver>() as MassStorageProvider)
    }

    viewModel{
        HostViewModel(get())
    }

}