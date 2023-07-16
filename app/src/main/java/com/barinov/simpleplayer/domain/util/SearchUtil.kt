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
) : EventProvider {

    companion object {
        const val DEFAULT_FOLDER_NAME = "untitled"
    }

    private val mutex = Mutex()

    private val utilScope = CoroutineScope(Job() + Dispatchers.IO)

    private val _filesEventFlow = fileWorker._filesEventFlow

    override val filesEventFlow = _filesEventFlow.asSharedFlow()

    val defaultInternalFolder: File = Environment.getExternalStorageDirectory()

    private var buffer: Pair<List<CommonFileItem>, String>? = null


    fun confirmAndHandle(
        copyOnInternalStorage: Boolean,
        onComplete: () -> Unit
    ) {
        utilScope.launch {
            mutex.withLock {
                buffer?.apply {
                    var totalCopied = 0L
                    first.forEachIndexed { index, commonFileItem ->
                        if (index == 0 && copyOnInternalStorage) {
                            _filesEventFlow.emit(
                                FileWorker.FileWorkEvents.OnCopyStarted(
                                    first.sumOf { it.len }.bytesToMb()
                                )
                            )
                        }
                        fileWorker.addValidMusicFile(
                            commonFileItem,
                            copyOnInternalStorage,
                            second,
                            totalCopied
                        )
                        totalCopied += commonFileItem.len
                    }
                    _filesEventFlow.emit(
                        FileWorker.FileWorkEvents.OnCompleted
                    )
                    onComplete.invoke()
                }
            }
        }
    }

    fun cancelResults() {
        utilScope.launch {
            mutex.withLock {
                _filesEventFlow.emit(FileWorker.FileWorkEvents.Idle)
                buffer = null
            }
        }
    }

    fun autoSearch(
        copyOnInternalStorage: Boolean,
        playListName: String?
    ) {
        utilScope.launch {
            mutex.withLock {
                _filesEventFlow.emit(FileWorker.FileWorkEvents.OnSearchStarted)
                val buffer = mutableListOf<CommonFileItem>()
                fileWorker.scanWithSubFolders(
                    defaultInternalFolder.toCommonFileItem(),
                    buffer,
                    playListName ?: DEFAULT_FOLDER_NAME
                )
                var totalCopied = 0L
                buffer.forEachIndexed { index, commonFileItem ->
                    if (index == 0 && copyOnInternalStorage) {
                        _filesEventFlow.emit(
                            FileWorker.FileWorkEvents.OnCopyStarted(
                                buffer.sumOf { it.len }.bytesToMb()
                            )
                        )
                    }
                    fileWorker.addValidMusicFile(
                        commonFileItem,
                        copyOnInternalStorage,
                        playListName ?: DEFAULT_FOLDER_NAME,
                        totalCopied
                    )
                    totalCopied += commonFileItem.len
                }
                _filesEventFlow.emit(
                    FileWorker.FileWorkEvents.OnSearchCompleted(
                        buffer.size
                    )
                )
            }
        }
    }


    fun searchWithSubFolders(
        folder: CommonFileItem,
        playListName: String?
    ) {
        utilScope.launch {
            mutex.withLock {
                _filesEventFlow.emit(FileWorker.FileWorkEvents.OnSearchStarted)
                val buffer = mutableListOf<CommonFileItem>()
                fileWorker.scanWithSubFolders(
                    folder,
                    buffer,
                    playListName ?: DEFAULT_FOLDER_NAME
                )
                if (buffer.isEmpty()) {
                    _filesEventFlow.emit(FileWorker.FileWorkEvents.NoMusicFound)
                } else {
                    this@SearchUtil.buffer = Pair(buffer, playListName ?: DEFAULT_FOLDER_NAME)
                    _filesEventFlow.emit(
                        FileWorker.FileWorkEvents.OnSearchCompleted(
                            buffer.size
                        )
                    )
                }
            }
        }
    }


    fun searchInFolder(
        folder: CommonFileItem,
        copyOnInternalStorage: Boolean,
        playListName: String?
    ) {
        utilScope.launch {
            mutex.withLock {
                val buffer = mutableListOf<CommonFileItem>()
                fileWorker.scanSelectedFolder(
                    folder,
                    buffer,
                    copyOnInternalStorage,
                    playListName
                )
                var totalCopied = 0L
                buffer.forEachIndexed { index, commonFileItem ->
                    if (index == 0 && copyOnInternalStorage) {
                        _filesEventFlow.emit(
                            FileWorker.FileWorkEvents.OnCopyStarted(
                                buffer.sumOf { it.len }.bytesToMb()
                            )
                        )
                    }
                    fileWorker.addValidMusicFile(
                        commonFileItem,
                        copyOnInternalStorage,
                        playListName ?: DEFAULT_FOLDER_NAME,
                        totalCopied
                    )
                    totalCopied += commonFileItem.len
                }
                _filesEventFlow.emit(
                    FileWorker.FileWorkEvents.OnSearchCompleted(
                        0
                    )
                )
            }
        }
    }


}