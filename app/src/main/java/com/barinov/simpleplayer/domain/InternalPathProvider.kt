package com.barinov.simpleplayer.domain

interface InternalPathProvider {

    fun getInternalStorageRootPath(): String
}