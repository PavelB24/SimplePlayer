package com.barinov.simpleplayer.broadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.barinov.simpleplayer.domain.MassStorageProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import me.jahnen.libaums.core.UsbMassStorageDevice
import me.jahnen.libaums.core.fs.FileSystem
import me.jahnen.libaums.core.fs.UsbFile

class UsbEventsBroadcastReceiver: BroadcastReceiver(), MassStorageProvider {


    companion object {
        const val ACTION_USB_PERMISSION = "com.barinov.simpleplayer.USB_PERMISSION"
        const val ACTION_USB_STATE = "android.hardware.usb.action.USB_STATE"
        const val ACTION_USB_STATE_KEY = "connect_state"
    }

    private val _mssStorageDeviceAccessibilityFlow =
        MutableStateFlow<MassStorageProvider.MassStorageState>(
            MassStorageProvider.MassStorageState.NotReady
        )

    override fun openFolder(uFile: UsbFile?) {
        TODO("Not yet implemented")
    }

    override fun getRoot(): Pair<FileSystem, UsbFile> {
        TODO("Not yet implemented")
    }

    override val mssStorageDeviceAccessibilityFlow = _mssStorageDeviceAccessibilityFlow.asSharedFlow()

    override fun onReceive(context: Context?, intent: Intent?) {

    }





}