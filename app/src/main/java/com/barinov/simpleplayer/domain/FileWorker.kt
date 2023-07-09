package com.barinov.simpleplayer.domain

import android.content.Context
import android.content.ContextWrapper
import com.barinov.simpleplayer.copyWithCallBack
import com.barinov.simpleplayer.domain.model.CommonFileItem
import com.barinov.simpleplayer.domain.model.MusicFile
import com.barinov.simpleplayer.toCommonFileItem
import kotlinx.coroutines.flow.MutableSharedFlow
import me.jahnen.libaums.core.fs.FileSystem
import me.jahnen.libaums.core.fs.UsbFile
import me.jahnen.libaums.core.fs.UsbFileStreamFactory
import java.io.File

class FileWorker(
    context: Context,
    private val musicRepository: MusicRepository,
    private val audioDataHandler: AudioDataHandler,
    private val mediaEngine: TrackRemover
) : InternalPathProvider {


    val _filesEventFlow = MutableSharedFlow<FileEvents>()


    private val folderName = "user_music"


    private val internalPath =
        (ContextWrapper(context).applicationInfo.dataDir + File.separator + folderName).also {
            File(it).apply {
                if (!isDirectory) {
                    mkdir()
                }
            }
        }


    suspend fun scanWithSubFolders(
        file: CommonFileItem,
        buffer: MutableList<CommonFileItem>,
        playListName: String?
    ) {
//        if (!file.isDirectory) throw IllegalArgumentException()
        if (file.rootType == RootType.INTERNAL) {
            file.iFile?.apply {
                if (isFile) {
                    if (name.endsWith(".mp3")) {
                        buffer.add(toCommonFileItem())
                    }
                } else {
                    listFiles()?.forEach {
                        scanWithSubFolders(
                            it.toCommonFileItem(),
                            buffer,
                            playListName
                        )
                    }
                }
            }
        } else {
            file.uEntity?.apply {
                if (!uFile.isDirectory) {
                    if (uFile.name.endsWith(".mp3")) {
                        buffer.add(this.uFile.toCommonFileItem(fs))
                    }
                } else {
                    uFile.listFiles().forEach {
                        scanWithSubFolders(
                            it.toCommonFileItem(fs),
                            buffer,
                            playListName
                        )
                    }
                }
            }
        }
    }


    suspend fun deleteFiles(mFiles: List<MusicFile>, deleteFile: Boolean) {
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


    private fun deleteFile(mFile: MusicFile, deleteFile: Boolean) {
        mFile.apply {
            if (file.isFile && deleteFile) {
                file.delete()
            }
        }
    }

    suspend fun addValidMusicFile(
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
            if (!copyOnInternalStorage) throw IllegalArgumentException()
            musicFile.uEntity?.apply {
                onExternalCopy(uFile, fs, playListName)
            }
        }
    }

    private suspend fun copyExternal(uMusicFile: CommonFileItem.UsbData): String {
        return copyExternal(uMusicFile.fs, uMusicFile.uFile)
    }

    private suspend fun copyExternal(fs: FileSystem, uMusicFile: UsbFile): String {
        val fileName = uMusicFile.name
        val newPath = "$internalPath${File.separator}$fileName"
        uMusicFile.apply {
            UsbFileStreamFactory.createBufferedInputStream(uMusicFile, fs).use { fis ->
                File(newPath).apply {
                    if (!isFile) {
                        createNewFile()
                    }
                    outputStream().use { fos ->
                        fis.copyWithCallBack(fos) {
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
        buffer: MutableList<CommonFileItem>,
        copyOnInternalStorage: Boolean,
        playListName: String?
    ) {
        if (musicFile.rootType == RootType.INTERNAL) {
            musicFile.iFile?.apply {
                if (!isDirectory) throw IllegalArgumentException()
                listFiles()?.forEach {
                    if (it.isFile) {
                        if (it.name.endsWith(".mp3")) {
                            buffer.add(toCommonFileItem())
                        }
                    }
                }
            }
        } else {
            musicFile.uEntity?.apply {
                if (!uFile.isDirectory) throw IllegalArgumentException()
                uFile.listFiles().forEach {
                    if (!it.isDirectory && it.name.endsWith(".mp3")) {
                        buffer.add(it.toCommonFileItem(fs))
                    }
                }

            }
        }
    }

    private suspend fun onExternalCopy(usbFile: UsbFile, fs: FileSystem, playListName: String?) {
        val finalPath = copyExternal(usbFile.toCommonFileItem(fs).uEntity!!)
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


    sealed interface FileEvents {

        object Idle: FileEvents

        data class Error(val e: Throwable): FileEvents

        data class OnCopyStarted(val megaBytesToCopy: Float): FileEvents

        data class OnBlockCopied(val megaBytes: Float): FileEvents

        data class OnSearchCompleted(val count: Int): FileEvents
    }

    override fun getInternalStorageRootPath(): String {
        TODO("Not yet implemented")
    }


}