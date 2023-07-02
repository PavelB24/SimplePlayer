package com.barinov.simpleplayer.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.barinov.simpleplayer.core.MediaController
import com.barinov.simpleplayer.service.PlayerMediaService
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.koin.android.ext.android.inject


class MainActivity : ComponentActivity() {

    private val mediaSessionAdapter: MediaController by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlayerTheme()
        }
        startService(Intent(this, PlayerMediaService::class.java))
    }

}



