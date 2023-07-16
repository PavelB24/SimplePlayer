package com.barinov.simpleplayer.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.barinov.simpleplayer.broadcastReceivers.UsbEventsBroadcastReceiver
import org.koin.android.ext.android.inject


class UsbConnectionService: Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    private val usbReceiver: UsbEventsBroadcastReceiver by inject()


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
//        registerReceiver(usbReceiver)
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(usbReceiver)
    }
}