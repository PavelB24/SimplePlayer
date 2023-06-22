package com.barinov.simpleplayer.broadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class UsbEventsBroadcastReceiver: BroadcastReceiver() {


    companion object {
        const val ACTION_USB_PERMISSION = "com.barinov.simpleplayer.USB_PERMISSION"
        const val ACTION_USB_STATE = "android.hardware.usb.action.USB_STATE"
        const val ACTION_USB_STATE_KEY = "connect_state"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        TODO("Not yet implemented")
    }
}