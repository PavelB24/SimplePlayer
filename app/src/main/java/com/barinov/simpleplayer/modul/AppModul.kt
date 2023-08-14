package com.barinov.simpleplayer.modul

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.barinov.simpleplayer.broadcastReceivers.UsbEventsBroadcastReceiver
import com.barinov.simpleplayer.core.MediaController
import com.barinov.simpleplayer.core.MediaEngine
import com.barinov.simpleplayer.data.LocalDataBase
import com.barinov.simpleplayer.data.TracksValidator
import com.barinov.simpleplayer.domain.AudioDataHandler
import com.barinov.simpleplayer.domain.FileWorker
import com.barinov.simpleplayer.domain.MassStorageProvider
import com.barinov.simpleplayer.domain.MusicRepository
import com.barinov.simpleplayer.domain.Player
import com.barinov.simpleplayer.domain.TrackRemover
import com.barinov.simpleplayer.domain.util.SearchUtil
import com.barinov.simpleplayer.prefs.PreferencesManager
import com.barinov.simpleplayer.ui.viewModels.FileBrowserViewModel
import com.barinov.simpleplayer.ui.viewModels.ScanViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

const val SHARED_PREFS_NAME = "simple_player_prefs"
const val DATA_BASE_NAME = "simple_player_db"

val module = module {

    single {
        SearchUtil(get(), get())
    }

    single {
        AudioDataHandler()
    }

    single {
        FileWorker(androidContext(), get(), get<MediaEngine>() as TrackRemover)
    }

    single {
        Room.databaseBuilder(
            androidApplication(),
            LocalDataBase::class.java,
            DATA_BASE_NAME
        ).addCallback(object: RoomDatabase.Callback(){
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                val validationWork = OneTimeWorkRequestBuilder<TracksValidator>().build()
                WorkManager.getInstance(androidContext()).enqueue(validationWork)
            }
        })
            .build()
    }

    single {
        UsbEventsBroadcastReceiver(androidContext())
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
        ScanViewModel(get(), get())
    }

}