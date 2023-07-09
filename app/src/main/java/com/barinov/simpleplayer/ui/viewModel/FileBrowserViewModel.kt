package com.barinov.simpleplayer.ui.viewModel

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barinov.simpleplayer.domain.RootType
import com.barinov.simpleplayer.domain.MassStorageProvider
import com.barinov.simpleplayer.domain.model.CommonFileItem
import com.barinov.simpleplayer.domain.util.SearchUtil
import com.barinov.simpleplayer.isFile
import com.barinov.simpleplayer.toCommonFileItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.jahnen.libaums.core.fs.UsbFile

class FileBrowserViewModel(
    private val massStorageProvider: MassStorageProvider,
    private val searchUtil: SearchUtil
) : ViewModel() {

    private val rootTypeFlow: MutableStateFlow<RootType> = MutableStateFlow(RootType.INTERNAL)

    private val massStorageState = massStorageProvider.mssStorageDeviceAccessibilityFlow

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
        } else{
            val massStorageRoot = massStorageProvider.getRoot()
            massStorageRoot.second.toCommonFileItem(massStorageRoot.first)
        }


    private fun getInternalRoot() = Environment.getExternalStorageDirectory()

    fun autoSearch() = searchUtil.autoSearch(false, null)

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

    fun changeRootType(rootType: RootType) {
        viewModelScope.launch(Dispatchers.IO) {
            rootTypeFlow.emit(rootType)
        }
    }

    fun isBackStackGoingToEmpty() = backStack.size - 1 == 0

    fun importFromCurrentFolder() {

    }

    fun peekFolder(folder: CommonFileItem? = null): ArrayList<CommonFileItem> {
        return arrayListOf(folder ?: currentFolder)
    }


}