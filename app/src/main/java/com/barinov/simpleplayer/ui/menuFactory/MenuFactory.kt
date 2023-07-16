package com.barinov.simpleplayer.ui.menuFactory

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import com.barinov.simpleplayer.R
import com.barinov.simpleplayer.domain.MassStorageProvider
import com.barinov.simpleplayer.domain.RootType
import com.barinov.simpleplayer.ui.AddFromCurrentDirImageButton
import com.barinov.simpleplayer.ui.ArgsContainer
import com.barinov.simpleplayer.ui.RotatingRootButton
import com.barinov.simpleplayer.ui.ExpandedMenu
import com.barinov.simpleplayer.ui.MenuFactoryI
import com.barinov.simpleplayer.ui.TopBarConnector

@Immutable
class MenuFactory(private val args: ArgsContainer.FileBrowserArgs) : MenuFactoryI {


    override fun getMenuInstance(
        connector: TopBarConnector,
    ): @Composable () -> Unit {
        return when (connector) {
            is TopBarConnector.FileBrowserTopBarConnector -> {
                {
                    FileBrowserMenu(
                        connector,
                        args
                    )
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
        args: ArgsContainer.FileBrowserArgs
    ) {
        val isUsbReady = args.usbAccessFlow.collectAsState()
        AnimatedVisibility(visible = isUsbReady.value is MassStorageProvider.MassStorageState.Ready) {
            RotatingRootButton(args.startRt, args.typeFlow){
                connector.changeRootType()
            }
        }
        AddFromCurrentDirImageButton() {
            connector.onFolderPeeked()
        }
    }


}