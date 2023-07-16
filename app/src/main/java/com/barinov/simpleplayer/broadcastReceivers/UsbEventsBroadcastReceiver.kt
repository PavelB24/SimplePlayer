package com.barinov.simpleplayer.broadcastReceivers

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import com.barinov.simpleplayer.domain.MassStorageProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import me.jahnen.libaums.core.fs.FileSystem
import me.jahnen.libaums.core.fs.UsbFile

class UsbEventsBroadcastReceiver(
    private val context: Context
) : BroadcastReceiver(), MassStorageProvider {


    private val usbManager = context.getSystemService(UsbManager::class.java)

    companion object {
        const val ACTION_USB_PERMISSION = "com.barinov.simpleplayer.USB_PERMISSION"
        const val ACTION_USB_STATE = "android.hardware.usb.action.USB_STATE"
        const val ACTION_USB_STATE_KEY = "connect_state"
    }

    private val _mssStorageDeviceAccessibilityFlow =
        MutableStateFlow<MassStorageProvider.MassStorageState>(
            MassStorageProvider.MassStorageState.NotReady
        )


    override val mssStorageDeviceAccessibilityFlow: StateFlow<MassStorageProvider.MassStorageState> = _mssStorageDeviceAccessibilityFlow.asStateFlow()

    init {
        usbManager.deviceList.entries.firstOrNull()?.value?.let { device->
            askPermission(device)
        }
    }

    override fun openFolder(uFile: UsbFile?) {
        uFile?.let {
            
        }
    }

    override fun getRoot(): Pair<FileSystem, UsbFile> {
        TODO("Not yet implemented")
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            when(it.action){
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        it.getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
                    } else {
                        it.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    }
                    device?.apply {
                        if (usbManager.hasPermission(this)) {
                            askPermission(this)
                        } else {

                        }
                    }
                }
                UsbManager.ACTION_USB_ACCESSORY_DETACHED -> {
                    close()
                }
                ACTION_USB_PERMISSION -> {
                    val device: UsbDevice? =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            it.getParcelableExtra(
                                UsbManager.EXTRA_DEVICE,
                                UsbDevice::class.java
                            )
                        } else {
                            it.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                        }
                    if(it.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        device?.let { initDevice(it) }
                    } else {

                    }
                }

                else -> {}
            }
        }
    }

    private fun close() {

    }

    private fun askPermission(device: UsbDevice) {
        if (usbManager.hasPermission(device)) {
            initDevice(device)
        } else {
            val permissionIntent =
                PendingIntent.getBroadcast(
                    context, 0, Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_MUTABLE
                )
            usbManager.requestPermission(device, permissionIntent)
        }
    }

    private fun initDevice(device: UsbDevice) {

    }


}