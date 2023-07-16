package com.barinov.simpleplayer.ui.menuFactory


import androidx.compose.runtime.Immutable
import com.barinov.simpleplayer.ui.ArgsContainer
import com.barinov.simpleplayer.ui.MenuFactoryI

@Immutable
abstract class AbstractMenuFactory {

    companion object{
       fun provide(args: ArgsContainer): MenuFactoryI{
            return when(args){
                is ArgsContainer.FileBrowserArgs -> MenuFactory(args)
                else -> {
                    throw IllegalArgumentException()
                }
            }
        }
    }

}