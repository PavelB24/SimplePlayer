package com.barinov.simpleplayer.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.barinov.simpleplayer.R

@Stable
object MenuFactory {


    fun getMenuInstance(
        connector: TopBarConnector
    ): @Composable () -> Unit {
        return when (connector) {
            is TopBarConnector.FileBrowserTopBarConnector -> {
                {
                    FileBrowserMenu(connector)
                }
            }

            is TopBarConnector.PlaylistsTopBarConnector -> {
                {
                    CurrentPlayListMenu(connector)
                }
            }
        }
    }


    @Composable
    private fun CurrentPlayListMenu(
        connector: TopBarConnector.PlaylistsTopBarConnector
    ) {
        val refs = arrayOf(R.string.load_tracks_menu_item)

        val onClick: (Int) -> Unit =
            { ref ->
                when (ref) {
                    R.string.load_tracks_menu_item -> connector.importTracks()
                    R.string.about_menu_item -> connector.showAboutDialog()
                }
            }
        ExpandedMenu(refs = refs, onClick = onClick)
    }


    @Composable
    private fun FileBrowserMenu(
        connector: TopBarConnector.FileBrowserTopBarConnector,
    ) {
        MenuImageButton(connector.icon) {
            connector.onFolderPeeked()
        }
    }


}