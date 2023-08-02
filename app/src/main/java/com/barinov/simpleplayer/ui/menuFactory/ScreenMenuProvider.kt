package com.barinov.simpleplayer.ui.menuFactory


import androidx.compose.runtime.Immutable
import com.barinov.simpleplayer.ui.MenuType
import com.barinov.simpleplayer.ui.MenuProvider

@Immutable
abstract class ScreenMenuProvider {

    companion object{
       fun provide(args: MenuType): MenuProvider{
            return when(args){
                is MenuType.FileBrowserType -> FileBrowserMenuProvider(args)
                is MenuType.ScanScreenType ->  ScanScreenMenuProvider()
                else -> {
                    throw IllegalArgumentException()
                }
            }
        }
    }

}