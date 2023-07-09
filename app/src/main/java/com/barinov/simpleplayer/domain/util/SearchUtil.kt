package com.barinov.simpleplayer.domain.util

import android.os.Environment
import com.barinov.simpleplayer.bytesToMb
import com.barinov.simpleplayer.domain.EventProvider
import com.barinov.simpleplayer.domain.FileWorker
import com.barinov.simpleplayer.domain.model.CommonFileItem
import com.barinov.simpleplayer.toCommonFileItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File

class SearchUtil(
    private val fileWorker: FileWorker
): EventProvider {
    private val mutex = Mutex()

    private val utilScope = CoroutineScope(Job() + Dispatchers.IO)

    private val _filesEventFlow = fileWorker._filesEventFlow

    override val filesEventFlow = _filesEventFlow.asSharedFlow()

    val defaultInternalFolder: File = Environment.getExternalStorageDirectory()


    fun autoSearch(
        copyOnInternalStorage: Boolean,
        playListName: String?
    ) {
        utilScope.launch {
            mutex.withLock {
                val buffer = mutableListOf<CommonFileItem>()
                fileWorker.scanWithSubFolders(
                    defaultInternalFolder.toCommonFileItem(),
                    buffer,
                    playListName
                )
                buffer.forEachIndexed { index, commonFileItem ->
                    if (index == 0 && copyOnInternalStorage){
                        _filesEventFlow.emit(
                            FileWorker.FileEvents.OnCopyStarted(
                                buffer.sumOf { it.len }.bytesToMb()
                            )
                        )
                    }
                    fileWorker.addValidMusicFile(commonFileItem, copyOnInternalStorage, playListName)
                }
                _filesEventFlow.emit(
                    FileWorker.FileEvents.OnSearchCompleted(
                        buffer.size
                    )
                )
            }
        }
    }


    fun searchWithSubFolders(
        file: CommonFileItem,
        copyOnInternalStorage: Boolean,
        playListName: String?
    ) {
        utilScope.launch {
            mutex.withLock {
                val buffer = mutableListOf<CommonFileItem>()
                fileWorker.scanWithSubFolders(
                    file,
                    buffer,
                    playListName
                )
                buffer.forEachIndexed { index, commonFileItem ->
                    if (index == 0 && copyOnInternalStorage){
                        _filesEventFlow.emit(
                            FileWorker.FileEvents.OnCopyStarted(
                                buffer.sumOf { it.len }.bytesToMb()
                            )
                        )
                    }
                    fileWorker.addValidMusicFile(commonFileItem, copyOnInternalStorage, playListName)
                }
                _filesEventFlow.emit(
                    FileWorker.FileEvents.OnSearchCompleted(
                        0
                    )
                )
            }
        }
    }


    fun searchInFolder(
        file: CommonFileItem,
        copyOnInternalStorage: Boolean,
        playListName: String?
    ) {
        utilScope.launch {
            mutex.withLock {
                val buffer = mutableListOf<CommonFileItem>()
                fileWorker.scanSelectedFolder(
                    file,
                    buffer,
                    copyOnInternalStorage,
                    playListName
                )
                buffer.forEachIndexed { index, commonFileItem ->
                    if (index == 0 && copyOnInternalStorage){
                        _filesEventFlow.emit(
                            FileWorker.FileEvents.OnCopyStarted(
                                buffer.sumOf { it.len }.bytesToMb()
                            )
                        )
                    }
                    fileWorker.addValidMusicFile(commonFileItem, copyOnInternalStorage, playListName)
                }
                _filesEventFlow.emit(
                    FileWorker.FileEvents.OnSearchCompleted(
                        0
                    )
                )
            }
        }
    }



}