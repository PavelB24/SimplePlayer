package com.barinov.simpleplayer.broadcastReceivers

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import com.barinov.simpleplayer.BuildConfig
import com.barinov.simpleplayer.domain.MassStorageProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.jahnen.libaums.core.UsbMassStorageDevice
import me.jahnen.libaums.core.fs.FileSystem
import me.jahnen.libaums.core.fs.UsbFile

class UsbEventsBroadcastReceiver(
    private val context: Context
) : BroadcastReceiver(), MassStorageProvider {


    companion object {
        const val ACTION_USB_PERMISSION = "com.barinov.simpleplayer.USB_PERMISSION"
        const val ACTION_USB_STATE = "android.hardware.usb.action.USB_STATE"
        const val ACTION_USB_STATE_KEY = "connect_state"
    }

    private var currentConnection: Pair<FileSystem, UsbMassStorageDevice>? = null

    private val usbManager = context.getSystemService(UsbManager::class.java)

    private val localScope = CoroutineScope(Job() + Dispatchers.IO)

    private val _massStorageDataFlow =
        MutableStateFlow<MassStorageProvider.MassStorageState>(
            MassStorageProvider.MassStorageState.NotReady
        )


    override val massStorageDataFlow: StateFlow<MassStorageProvider.MassStorageState> =
        _massStorageDataFlow.asStateFlow()

    init {
        localScope.launch {
            UsbMassStorageDevice.getMassStorageDevices(context).firstOrNull()?.let { device ->
                if (usbManager.hasPermission(device.usbDevice)) {
                    initDevice(device)
                } else {
                    askPermission(device.usbDevice)
                }
            }
        }
    }

    override suspend fun openFolder(uFile: UsbFile?) {
        uFile?.let {
            try {
                _massStorageDataFlow.emit(
                    MassStorageProvider.MassStorageState.Ready(
                        (currentConnection?.first ?: throw IllegalArgumentException()) to it.listFiles()
                    )
                )
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
                _massStorageDataFlow.emit(MassStorageProvider.MassStorageState.NotReady)
            }

        }
    }

    override fun getRoot(): Pair<FileSystem, UsbFile>? {
        return currentConnection?.run {
            return@run first to first.rootDirectory
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            localScope.launch {
                when (it.action) {
                    ACTION_USB_STATE -> {
                        if (!it.getBooleanExtra(ACTION_USB_STATE_KEY, true) &&
                            checkOnUsbStorage()
                        ) {
                            _massStorageDataFlow.emit(MassStorageProvider.MassStorageState.NotReady)
                            close()
                        }
                    }

                    UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                        val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            it.getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
                        } else {
                            it.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                        }
                        device?.apply {
                            if (!usbManager.hasPermission(this)) {
                                askPermission(this)
                            } else {
                                initDevice(this)
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
                        if (it.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            device?.let { initDevice(it) }
                        } else {

                        }
                    }

                    else -> {}
                }
            }
        }
    }

    private fun checkOnUsbStorage() =
        UsbMassStorageDevice.getMassStorageDevices(context)
            .isEmpty()

    private fun close() {
        currentConnection?.second?.close()
        currentConnection = null
    }

    private fun askPermission(device: UsbDevice) {
        if (usbManager.hasPermission(device)) {
            localScope.launch {
                initDevice(device)
            }
        } else {
            val permissionIntent =
                PendingIntent.getBroadcast(
                    context, 0, Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_MUTABLE
                )
            usbManager.requestPermission(device, permissionIntent)
        }
    }


    private suspend fun initDevice(device: UsbMassStorageDevice){
        device.init()
        val currentFs: FileSystem? = device.partitions.firstOrNull()?.fileSystem
        currentFs?.let { fs ->
            currentConnection = fs to device
            _massStorageDataFlow.emit(
                MassStorageProvider.MassStorageState.Ready(
                    fs to fs.rootDirectory.listFiles()
                )
            )
        }
    }

    private suspend fun initDevice(device: UsbDevice) {
        try {
            UsbMassStorageDevice.getMassStorageDevices(context).find {
                it.usbDevice.deviceId == device.deviceId
            }?.let { massStorage ->
                initDevice(massStorage)
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            _massStorageDataFlow.emit(
                MassStorageProvider.MassStorageState.NotReady
            )
        }
    }

}