package com.barinov.simpleplayer.domain

import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import com.barinov.simpleplayer.bytesToMb
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


    val _filesEventFlow = MutableSharedFlow<FileWorkEvents>()


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
        playListName: String
    ) {
//        if (!file.isDirectory) throw IllegalArgumentException()
        if (file.rootType == RootType.INTERNAL) {
            file.iFile?.apply {
                if (isFile) {
                    if (name.endsWith(".mp3") &&
                        !musicRepository.findByName(name, playListName)
                    ) {
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
        playListName: String,
        alreadyCopied: Long,
    ) {
        if (musicFile.rootType == RootType.INTERNAL) {
            musicFile.iFile?.apply {
                if (!isFile) throw IllegalArgumentException()
                val metadata = audioDataHandler.getMusicFileMetaData(this)
                var finalPath = path
                if (copyOnInternalStorage) {
                    finalPath = copyInInternal( alreadyCopied, this)
                }
                musicRepository.addFileIndex(metadata, finalPath, playListName)
            }
        } else {
            if (!copyOnInternalStorage) throw IllegalArgumentException()
            musicFile.uEntity?.apply {
                onExternalCopy(alreadyCopied, uFile, fs, playListName)
            }
        }
    }

    private suspend fun copyExternal(alreadyCopied: Long, uMusicFile: CommonFileItem.UsbData): String {
        return copyExternal(alreadyCopied, uMusicFile.fs, uMusicFile.uFile)
    }

    private suspend fun copyExternal(
        alreadyCopied: Long,
        fs: FileSystem,
        uMusicFile: UsbFile
    ): String {
        val fileName = uMusicFile.name
        val newPath = "$internalPath${File.separator}$fileName"
        uMusicFile.apply {
            UsbFileStreamFactory.createBufferedInputStream(uMusicFile, fs).use { fis ->
                File(newPath).apply {
                    if (!isFile) {
                        createNewFile()
                    }
                    outputStream().use { fos ->
                        fis.copyWithCallBack(alreadyCopied, fos) {
                            _filesEventFlow.emit(FileWorkEvents.OnBlockCopied(it))
                        }
                    }
                }
            }
        }
        return newPath
    }

    private suspend fun copyInInternal(
        alreadyCopied: Long,
        musicFile: File
    ):  String {
        Log.d("@@@", "COPY $musicFile")
        val fileName = musicFile.name
        val newPath = "$internalPath${File.separator}$fileName"
        File(newPath).also { newFile ->
            if (!newFile.isFile) {
                newFile.createNewFile()
            } else {
                newFile.delete()
                newFile.createNewFile()
            }
            musicFile.inputStream().use { fis ->
                newFile.outputStream().use { fos ->
                    Log.d("@@@", "COPY ${fis.available().toLong().bytesToMb()}")
                    fis.copyWithCallBack(alreadyCopied, fos) {
                        _filesEventFlow.emit(FileWorkEvents.OnBlockCopied(it))
                    }
                    return newPath
                }
            }
        }
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

    private suspend fun onExternalCopy(
        alreadyCopied: Long,
        usbFile: UsbFile, fs: FileSystem, playListName: String?) {
        val finalPath = copyExternal(alreadyCopied, usbFile.toCommonFileItem(fs).uEntity!!)
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


    sealed interface FileWorkEvents {

        object OnSearchStarted : FileWorkEvents

        object Idle : FileWorkEvents

        object OnCompleted : FileWorkEvents

        object NoMusicFound : FileWorkEvents

        data class Error(val e: Throwable) : FileWorkEvents

        data class OnCopyStarted(val megaBytesToCopy: Int) : FileWorkEvents

        data class OnBlockCopied(val megaBytes: Int) : FileWorkEvents

        data class OnSearchCompleted(val count: Int) : FileWorkEvents
    }

    override fun getInternalStorageRootPath(): String {
        TODO("Not yet implemented")
    }


}