package com.barinov.simpleplayer.ui

import androidx.compose.ui.graphics.vector.ImageVector
import com.barinov.simpleplayer.domain.RootType

sealed interface TopBarConnector{

    abstract class FileBrowserTopBarConnector(): TopBarConnector{

        abstract fun onFolderPeeked()

        abstract fun changeRootType()


    }

    interface PlaylistsTopBarConnector: TopBarConnector{

        fun showAboutDialog()

        fun importTracks()

    }

}