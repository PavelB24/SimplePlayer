package com.barinov.simpleplayer.ui.menuFactory

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import com.barinov.simpleplayer.R
import com.barinov.simpleplayer.domain.MassStorageProvider
import com.barinov.simpleplayer.ui.components.AddFromCurrentDirImageButton
import com.barinov.simpleplayer.ui.MenuType
import com.barinov.simpleplayer.ui.components.RotatingRootButton
import com.barinov.simpleplayer.ui.components.ExpandedMenu
import com.barinov.simpleplayer.ui.MenuProvider
import com.barinov.simpleplayer.ui.TopBarConnector

@Immutable
class FileBrowserMenuProvider(
    private val args: MenuType.FileBrowserType
): MenuProvider {


    override fun getInstance(
        connector: TopBarConnector?
    ): @Composable () -> Unit {
         when (connector) {
            is TopBarConnector.FileBrowserTopBarConnector -> {
               return {
                    FileBrowserMenu(
                        connector,
                        args
                    )
                }
            } else -> {
                throw IllegalArgumentException("${this::class.java.name} incomparable with this TopBarConnector")
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
        args: MenuType.FileBrowserType
    ) {
        val isUsbReady = args.usbAccessFlow.collectAsState()
        AnimatedVisibility(visible = isUsbReady.value is MassStorageProvider.MassStorageState.Ready) {
            RotatingRootButton(args.startRt, args.typeFlow) {
                connector.changeRootType()
            }
        }
        AddFromCurrentDirImageButton() {
            connector.onFolderPeeked()
        }
    }


}