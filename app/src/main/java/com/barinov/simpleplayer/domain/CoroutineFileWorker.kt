package com.barinov.simpleplayer.domain

import android.content.Context
import android.content.ContextWrapper
import com.barinov.simpleplayer.copyWithCallBack
import com.barinov.simpleplayer.core.MediaEngine
import com.barinov.simpleplayer.domain.model.CommonFileItem
import com.barinov.simpleplayer.domain.model.MusicFile
import com.barinov.simpleplayer.toCommonFileItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.jahnen.libaums.core.fs.FileSystem
import me.jahnen.libaums.core.fs.UsbFile
import me.jahnen.libaums.core.fs.UsbFileStreamFactory
import java.io.File

class CoroutineFileWorker(
    context: Context,
    private val musicRepository: MusicRepository,
    private val audioDataHandler: AudioDataHandler,
    private val mediaEngine: TrackRemover
) {

    private val fileUtilScope = CoroutineScope(Job() + Dispatchers.IO)

    private val _filesEventFlow = MutableSharedFlow<FileEvents>()
    private val filesEventFlow = _filesEventFlow.asSharedFlow()


    private val folderName = "user_music"
    private val mutex = Mutex()

    private val internalPath =
        (ContextWrapper(context).applicationInfo.dataDir + File.separator + folderName).also {
            File(it).apply {
                if (!isDirectory) {
                    mkdir()
                }
            }
        }


    fun scanWithSubFolders(
        file: CommonFileItem,
        copyOnInternalStorage: Boolean,
        playListName: String?
    ) {
//        if (!file.isDirectory) throw IllegalArgumentException()
        fileUtilScope.launch {
            if (file.rootType == RootType.INTERNAL) {
                file.iFile?.apply {
                    if (isFile) {
                        if (name.endsWith(".mp3")) {
                            fileUtilScope.launch {
                                mutex.withLock {
                                    addValidMusicFile(file, copyOnInternalStorage, playListName)
                                }
                            }
                        }
                    } else {
                        listFiles()?.forEach {
                            scanWithSubFolders(
                                it.toCommonFileItem(),
                                copyOnInternalStorage,
                                playListName
                            )
                        }
                    }
                }
            } else {
                file.uFile?.apply {
                    if (!copyOnInternalStorage) throw IllegalArgumentException()
                    if (!uFile.isDirectory) {
                        if (uFile.name.endsWith(".mp3")) {
                            fileUtilScope.launch {
                                mutex.withLock {
                                    addValidMusicFile(file, true, playListName)
                                }
                            }
                        }
                    } else {
                        uFile.listFiles().forEach {
                            scanWithSubFolders(it.toCommonFileItem(fs), true, playListName)
                        }
                    }
                }
            }
        }
    }

    fun deleteFiles(mFiles: List<MusicFile>, deleteFile: Boolean) {
        fileUtilScope.launch {
            mutex.withLock {
                mFiles.forEach {
                    mediaEngine.deleteFromCurrentPlayBack(it)
                    if (mediaEngine.getCurrentTrackId() != it.id) {
                        deleteFile(it, deleteFile)
                        musicRepository.deleteMusicFileIndexById(it.id)
                    } else {
                        deleteFile(it, deleteFile)
                    }
                }
            }
        }
    }

    fun deleteFile(mFile: MusicFile, deleteFile: Boolean) {
        mFile.apply {
            if (file.isFile && deleteFile) {
                file.delete()
            }
        }
    }

    private suspend fun addValidMusicFile(
        musicFile: CommonFileItem,
        copyOnInternalStorage: Boolean,
        playListName: String?
    ) {
        if (musicFile.rootType == RootType.INTERNAL) {
            musicFile.iFile?.apply {
                if (!isDirectory) throw IllegalArgumentException()
                val metadata = audioDataHandler.getMusicFileMetaData(this)
                var finalPath = path
                if (copyOnInternalStorage) {
                    finalPath = copyInInternal(this)
                }
                musicRepository.addFileIndex(metadata, finalPath, playListName)
            }
        } else {
            if(!copyOnInternalStorage) throw IllegalArgumentException()
            musicFile.uFile?.apply {
                onExternalCopy(uFile, fs, playListName)
            }
        }
    }

    private suspend fun copyExternal(uMusicFile: CommonFileItem.UsbData): String{
        return copyExternal(uMusicFile.fs, uMusicFile.uFile)
    }

     private suspend fun copyExternal(fs: FileSystem, uMusicFile: UsbFile): String{
        val fileName = uMusicFile.name
        val newPath = "$internalPath${File.separator}$fileName"
        uMusicFile.apply {
            UsbFileStreamFactory.createBufferedInputStream(uMusicFile, fs).use { fis->
                File(newPath).apply {
                    if(!isFile){
                        createNewFile()
                    }
                    outputStream().use { fos->
                        fis.copyWithCallBack(fos){
                            _filesEventFlow.emit(FileEvents.OnBlockCopied(it))
                        }
                    }
                }
            }
        }
        return newPath
    }

    private suspend fun copyInInternal(musicFile: File): String {
        val fileName = musicFile.name
        val newPath = "$internalPath${File.separator}$fileName"
        File(newPath).also { newFile ->
            if (!newFile.isFile) {
                newFile.createNewFile()
            }
            musicFile.inputStream().use { fis ->
                newFile.outputStream().use { fos ->
                    fis.copyWithCallBack(fos) {
                        _filesEventFlow.emit(FileEvents.OnBlockCopied(it))
                    }
                }
            }
        }
        return newPath
    }


    fun scanSelectedFolder(
        musicFile: CommonFileItem,
        copyOnInternalStorage: Boolean,
        playListName: String?
    ) {
        fileUtilScope.launch {
            mutex.withLock {
                if (musicFile.rootType == RootType.INTERNAL) {
                    musicFile.iFile?.apply {
                        if (!isDirectory) throw IllegalArgumentException()
                        listFiles()?.forEach {
                            if (it.isFile) {
                                if (it.name.endsWith(".mp3")) {
                                    addValidMusicFile(
                                        it.toCommonFileItem(),
                                        copyOnInternalStorage,
                                        playListName
                                    )
                                }
                            }
                        }
                    }
                } else {
                    musicFile.uFile?.apply {
                        if (!uFile.isDirectory) throw IllegalArgumentException()
                        uFile.listFiles().forEach {
                            onExternalCopy(it, fs, playListName)
                        }

                    }
                }
            }
        }
    }

    private suspend fun onExternalCopy(usbFile: UsbFile, fs: FileSystem, playListName: String?){
        val finalPath = copyExternal(usbFile.toCommonFileItem(fs).uFile!!)
        val metadata = audioDataHandler.getMusicFileMetaData(File(finalPath))
        musicRepository.addFileIndex(metadata, finalPath, playListName)
    }


    fun checkExist(paths: List<String>): List<File> {
        val existingFiles = mutableListOf<File>()
        paths.map { File(it) }.forEach {
            if (it.isFile) {
                existingFiles.add(it)
            }
        }
        return existingFiles
    }


    fun checkExist(path: String): Boolean = File(path).isFile

    fun clearInternalStorage() {
        File(internalPath).apply {
            delete()
            mkdir()
        }
    }

    enum class RootType() {
        INTERNAL, USB
    }

    sealed class FileEvents() {

        data class OnCopyStarted(val bytesToCopy: Long) : FileEvents()

        data class OnBlockCopied(val totalCopiedSize: Long) : FileEvents()
    }


}