package com.barinov.simpleplayer.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import android.os.IBinder
import android.util.Log
import com.barinov.simpleplayer.broadcastReceivers.UsbEventsBroadcastReceiver
import org.koin.android.ext.android.inject


class UsbConnectionService: Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    private val usbReceiver: UsbEventsBroadcastReceiver by inject()


    override fun onCreate() {
        Log.d("@@@", "onCreate")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("@@@", "STARTED")
        registerReceiver(usbReceiver, IntentFilter().also {
            it.addAction(UsbEventsBroadcastReceiver.ACTION_USB_PERMISSION)
            it.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            it.addAction(UsbEventsBroadcastReceiver.ACTION_USB_STATE)
        })
        return super.onStartCommand(intent, flags, startId)

    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(usbReceiver)
    }
}