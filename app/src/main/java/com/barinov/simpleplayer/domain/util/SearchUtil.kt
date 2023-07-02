package com.barinov.simpleplayer.domain.util

import android.os.Environment
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

class SearchUtil(
    private val fileWorker: FileWorker
): EventProvider {
    private val mutex = Mutex()

    private val utilScope = CoroutineScope(Job() + Dispatchers.IO)

    private val _filesEventFlow = fileWorker._filesEventFlow

    override val filesEventFlow = _filesEventFlow.asSharedFlow()

    fun autoSearch(
        copyOnInternalStorage: Boolean,
        playListName: String?
    ) {
        utilScope.launch {
            mutex.withLock {
                val buffer = mutableListOf<CommonFileItem>()
                fileWorker.scanWithSubFolders(
                    Environment.getExternalStorageDirectory().toCommonFileItem(),
                    buffer,
                    playListName
                )
                buffer.forEachIndexed { index, commonFileItem ->
                    if (index == 0 && copyOnInternalStorage){
                        _filesEventFlow.emit(
                            FileWorker.FileEvents.OnCopyStarted(
                                buffer.sumOf { it.len }
                            )
                        )
                    }
                    fileWorker.addValidMusicFile(commonFileItem, copyOnInternalStorage, playListName)
                }
                _filesEventFlow.emit(
                    FileWorker.FileEvents.OnSearchCompleted(
                        ""
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
                                buffer.sumOf { it.len }
                            )
                        )
                    }
                    fileWorker.addValidMusicFile(commonFileItem, copyOnInternalStorage, playListName)
                }
                _filesEventFlow.emit(
                    FileWorker.FileEvents.OnSearchCompleted(
                        ""
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
                                buffer.sumOf { it.len }
                            )
                        )
                    }
                    fileWorker.addValidMusicFile(commonFileItem, copyOnInternalStorage, playListName)
                }
                _filesEventFlow.emit(
                    FileWorker.FileEvents.OnSearchCompleted(
                        ""
                    )
                )
            }
        }
    }

}