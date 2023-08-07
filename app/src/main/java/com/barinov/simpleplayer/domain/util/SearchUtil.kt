package com.barinov.simpleplayer.domain.util

import android.os.Environment
import com.barinov.simpleplayer.bytesToMb
import com.barinov.simpleplayer.domain.EventProvider
import com.barinov.simpleplayer.domain.FileWorker
import com.barinov.simpleplayer.domain.MusicRepository
import com.barinov.simpleplayer.domain.MusicStorageType
import com.barinov.simpleplayer.domain.StorageDataCreator
import com.barinov.simpleplayer.domain.model.CommonFileItem
import com.barinov.simpleplayer.getName
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
    private val fileWorker: FileWorker,
    private val musicRepository: MusicRepository
) : EventProvider {

    companion object {
        const val DEFAULT_PLAYLIST_NAME = "untitled"
    }

    private val mutex = Mutex()

    private val creator: StorageDataCreator by lazy { StorageDataCreator() }

    private val utilScope = CoroutineScope(Job() + Dispatchers.IO)

    private val _filesEventFlow = fileWorker._filesEventFlow

    override val filesEventFlow = _filesEventFlow.asSharedFlow()

    val defaultInternalFolder: File = Environment.getExternalStorageDirectory()

    private var buffer: Pair<List<CommonFileItem>, String>? = null


    fun confirmAndHandle(
        playListName: String,
        copyOnInternalStorage: Boolean,
        onComplete: () -> Unit
    ) {
        utilScope.launch {
            mutex.withLock {
                buffer?.apply {
                    var totalCopied = 0L
                    val id: String =
                        musicRepository.createPlayListIfNeedAndGetId(playListName.ifEmpty { DEFAULT_PLAYLIST_NAME }) {
                            creator.createPlaylistEntity(it)
                        }
                    first.forEachIndexed { index, commonFileItem ->
                        if (index == 0) {
                            _filesEventFlow.emit(
                                FileWorker.FileWorkEvents.OnCopyStarted(
                                    if (copyOnInternalStorage)
                                        first.sumOf { it.len }.bytesToMb()
                                    else null
                                )
                            )
                        }
                        fileWorker.addValidMusicFile(
                            commonFileItem,
                            copyOnInternalStorage,
                            totalCopied
                        ) { metaData, path ->
                            musicRepository.addFileIndex(
                                creator.createTrackEntity(
                                    metaData,
                                    path,
                                    id,
                                    if (copyOnInternalStorage) MusicStorageType.IN_APP else MusicStorageType.EXTERNAL
                                )
                            )
                        }
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
                val id: String =
                    musicRepository.createPlayListIfNeedAndGetId(playListName ?: DEFAULT_PLAYLIST_NAME ) {
                        creator.createPlaylistEntity(playListName ?: DEFAULT_PLAYLIST_NAME)
                    }
                _filesEventFlow.emit(FileWorker.FileWorkEvents.OnSearchStarted)
                val buffer = mutableListOf<CommonFileItem>()
                fileWorker.scanWithSubFolders(
                    defaultInternalFolder.toCommonFileItem(),
                    buffer
                ) { signature ->
                    musicRepository.findByName(
                        signature.title,
                        playListName ?: DEFAULT_PLAYLIST_NAME,
                        signature.bitrate,
                        signature.duration
                    )
                }
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
                        totalCopied
                    ) { metaData, path ->
                        musicRepository.addFileIndex(
                            creator.createTrackEntity(
                                metaData,
                                path,
                                id,
                                if (copyOnInternalStorage) MusicStorageType.IN_APP else MusicStorageType.EXTERNAL
                            )

                        )
                    }
                    totalCopied += commonFileItem.len
                }
                _filesEventFlow.emit(
                    FileWorker.FileWorkEvents.OnSearchCompleted(
                        buffer.map { it.getName() }
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
                    buffer
                ) { signature ->
                    musicRepository.findByName(
                        signature.title,
                        playListName ?: DEFAULT_PLAYLIST_NAME,
                        signature.bitrate,
                        signature.duration
                    )
                }
                if (buffer.isEmpty()) {
                    _filesEventFlow.emit(FileWorker.FileWorkEvents.NoMusicFound)
                } else {
                    val  filteredBuffer = buffer.distinctBy { it.signatureString }
                    this@SearchUtil.buffer = filteredBuffer to (playListName ?: DEFAULT_PLAYLIST_NAME)
                    _filesEventFlow.emit(
                        FileWorker.FileWorkEvents.OnSearchCompleted(
                            filteredBuffer.map { it.getName() }
                        )
                    )
                }
            }
        }
    }


    fun searchInFolder(
        folder: CommonFileItem,
        copyOnInternalStorage: Boolean,
        playListName: String
    ) {
        utilScope.launch {
            mutex.withLock {
                val id: String =
                    musicRepository.createPlayListIfNeedAndGetId(playListName.ifEmpty { DEFAULT_PLAYLIST_NAME }) {
                        creator.createPlaylistEntity(playListName)
                    }
                val buffer = mutableListOf<CommonFileItem>()
                fileWorker.scanSelectedFolder(
                    folder,
                    buffer
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
                        totalCopied
                    ) { metaData, path ->
                        musicRepository.addFileIndex(
                            creator.createTrackEntity(
                                metaData,
                                path,
                                id,
                                if (copyOnInternalStorage) MusicStorageType.IN_APP else MusicStorageType.EXTERNAL
                            )
                        )
                    }
                    totalCopied += commonFileItem.len
                }
                _filesEventFlow.emit(
                    FileWorker.FileWorkEvents.OnSearchCompleted(
                        emptyList()
                    )
                )
            }
        }
    }


}