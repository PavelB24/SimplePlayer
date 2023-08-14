package com.barinov.simpleplayer.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.barinov.simpleplayer.service.PlayerMediaService
import com.barinov.simpleplayer.service.UsbConnectionService


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startService(Intent(this, UsbConnectionService::class.java))
        setContent {
            PlayerTheme()
        }
//        startService(Intent(this, PlayerMediaService::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, UsbConnectionService::class.java))
    }

}



