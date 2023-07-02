package com.barinov.simpleplayer.ui

import androidx.compose.ui.graphics.vector.ImageVector

sealed interface TopBarConnector{

    abstract class FileBrowserTopBarConnector(): TopBarConnector{

        abstract fun onFolderPeeked()

    }

    interface PlaylistsTopBarConnector: TopBarConnector{

        fun showAboutDialog()

        fun importTracks()

    }

}