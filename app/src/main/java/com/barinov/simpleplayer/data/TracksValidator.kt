package com.barinov.simpleplayer.data

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.barinov.simpleplayer.BuildConfig
import com.barinov.simpleplayer.domain.MusicRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.lang.Exception

class TracksValidator(
    context: Context,
    workerParams: WorkerParameters
): CoroutineWorker(context, workerParams), KoinComponent {

    private val tracksRepo: MusicRepository by inject()

    override suspend fun doWork(): Result {
        return try {
            checkExisting()
        } catch (e: Exception){
            if(BuildConfig.DEBUG){
                e.printStackTrace()
            }
            Result.failure()
        }
    }

    private suspend fun checkExisting(): Result {
        tracksRepo.getAllExternalTracksInfo().forEach {
            if(!File(it.path).isFile){
                tracksRepo.deleteMusicFileIndexById(it.id)
                if(tracksRepo.getTracksCountInPlayList(it.playListId) < 1){
                    tracksRepo.deletePlayList(it.playListId)
                }
            }
        }
        return Result.success()
    }
}