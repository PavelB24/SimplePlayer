package com.barinov.simpleplayer.ui.viewModel

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barinov.simpleplayer.domain.RootType
import com.barinov.simpleplayer.domain.MassStorageProvider
import com.barinov.simpleplayer.domain.model.CommonFileItem
import com.barinov.simpleplayer.isFile
import com.barinov.simpleplayer.toCommonFileItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FileBrowserViewModel(
    private val massStorageProvider: MassStorageProvider,
//    private val searchUtil: SearchUtil
) : ViewModel() {

    private val rootTypeFlow: MutableStateFlow<RootType> = MutableStateFlow(RootType.INTERNAL)

    val massStorageState = massStorageProvider.massStorageDataFlow

    private val internalFilesFlow =
        MutableStateFlow(getInternalRoot().listFiles()?.map { it.toCommonFileItem() } ?: listOf())

    private val backStack = ArrayDeque<CommonFileItem>()

    val filesFlow: StateFlow<List<CommonFileItem>> =
//        internalFilesFlow.shareIn(viewModelScope, SharingStarted.Eagerly)
        combine(rootTypeFlow, massStorageState, internalFilesFlow) { rt, extState, intFiles ->
            if (rt == RootType.INTERNAL) {
                intFiles
            } else {
                when (extState) {
                    MassStorageProvider.MassStorageState.NotReady -> intFiles
                    is MassStorageProvider.MassStorageState.Ready -> extState.uFiles.second.map {
                        it.toCommonFileItem(extState.uFiles.first)
                    }
//                    is MassStorageProvider.MassStorageState.Ready -> intFiles
                }
            }
        }
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                getInternalRoot().listFiles()?.map { it.toCommonFileItem() } ?: listOf())


    private var currentFolder =
        if (rootTypeFlow.value == RootType.INTERNAL) {
            Environment.getExternalStorageDirectory().toCommonFileItem()
        } else {
            val massStorageRoot = massStorageProvider.getRoot()
            massStorageRoot?.second?.toCommonFileItem(massStorageRoot.first)
        }


    private fun getInternalRoot() = Environment.getExternalStorageDirectory()

//    fun autoSearch() = searchUtil.autoSearch(false, null)

    fun onFolderClicked(folder: CommonFileItem, addInStack: Boolean = true) {
        viewModelScope.launch(Dispatchers.IO) {
            currentFolder = folder
            if (folder.isFile()) throw IllegalArgumentException("It's not a folder")
            if (addInStack) backStack.addLast(getParent(folder) ?: throw IllegalArgumentException())
            if (rootTypeFlow.value == RootType.INTERNAL) {
                folder.iFile?.listFiles()?.let {
                    internalFilesFlow.emit(it.map { file ->
                        file.toCommonFileItem()
                    })
                }
            } else {
                massStorageProvider.openFolder(folder.uEntity?.uFile)
            }
        }
    }

    fun getRtFlow() = rootTypeFlow.asStateFlow()

    private fun getParent(folder: CommonFileItem): CommonFileItem? {
        return if (folder.rootType == RootType.INTERNAL) {
            folder.iFile?.parentFile?.run {
                toCommonFileItem()
            }
        } else {
            folder.uEntity?.run {
                this.uFile.parent?.toCommonFileItem(fs)
            }
        }
    }

    fun goBack() {
        val last = backStack.removeLast()
        currentFolder = last
        onFolderClicked(last, false)
    }

    fun changeRootType() {
        rootTypeFlow.value =
            if(rootTypeFlow.value == RootType.INTERNAL)
                RootType.USB
            else
                RootType.INTERNAL
    }

    fun getRootType() = rootTypeFlow.value

    fun isBackStackGoingToEmpty() = backStack.size - 1 == 0

    fun importFromCurrentFolder() {

    }

    fun peekFolder(folder: CommonFileItem? = null): Array<CommonFileItem> {
        val entry = folder ?: currentFolder
        return if(entry == null){
            emptyArray()
        } else {
            arrayOf(entry)
        }
    }


}