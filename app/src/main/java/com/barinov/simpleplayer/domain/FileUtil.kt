package com.barinov.simpleplayer.domain

import java.io.File

class FileUtil(
    private val musicRepository: MusicRepository
) {


    fun exportWithSubFolders(file: File){
//        if (!file.isDirectory) throw IllegalArgumentException()
        if(file.isFile){
            if(file.name.endsWith("mp3")){
                addValidMusicFile(file)
            }
        } else {
            file.listFiles()?.forEach {
                exportWithSubFolders(it)
            }
        }

    }

    private fun addValidMusicFile(musicFile: File){
        if (!musicFile.isDirectory) throw IllegalArgumentException()
    }



    fun exportFromSelectedFolder(folder: File){

    }


    fun checkExist(paths: List<String>): List<File>{
        val existingFiles = mutableListOf<File>()
        paths.map { File(it) }.forEach {
            if(it.isFile){
                existingFiles.add(it)
            }
        }
        return existingFiles
    }

    fun checkExist(path: String): Boolean{
        return File(path).isFile
    }

    suspend fun clearInternalStorage(){

    }


    fun selectPathForPlaying(){}

}