package com.barinov.simpleplayer.domain

import kotlinx.coroutines.flow.SharedFlow

interface EventProvider {

    val filesEventFlow: SharedFlow<FileWorker.FileWorkEvents>

}
