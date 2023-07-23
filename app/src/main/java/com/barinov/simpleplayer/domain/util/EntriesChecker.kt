package com.barinov.simpleplayer.domain.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.barinov.simpleplayer.BuildConfig
import com.barinov.simpleplayer.domain.MusicRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.lang.Exception

class EntriesChecker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams), KoinComponent {

    private val repository: MusicRepository by inject()

    override suspend fun doWork(): Result {
        if (
            context.checkSelfPermission(Manifest.permission.READ_MEDIA_AUDIO) ==
            PackageManager.PERMISSION_DENIED
        ) {
            return Result.failure()
        }
        return try {
            repository.getAllTrackPaths().forEach { checkInfo ->
                File(checkInfo.path).apply {
                    if (!isFile) {
                        repository.deleteMusicFileIndexById(checkInfo.id)
                    }
                }

            }
            Result.success()
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            Result.failure()

        }

    }


}