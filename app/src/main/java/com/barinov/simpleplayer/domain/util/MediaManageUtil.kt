package com.barinov.simpleplayer.domain.util

import com.barinov.simpleplayer.domain.FileWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.sync.Mutex

class MediaManageUtil(
    private val fileWorker: FileWorker
) {

    private val mutex = Mutex()

    private val utilScope = CoroutineScope(Job() + Dispatchers.IO)

    private val _filesEventFlow = fileWorker._filesEventFlow

    val filesEventFlow = _filesEventFlow.asSharedFlow()
}