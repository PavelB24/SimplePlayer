package com.barinov.simpleplayer.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.barinov.simpleplayer.core.MediaController
import com.barinov.simpleplayer.service.PlayerMediaService
import com.barinov.simpleplayer.ui.theme.SimplePlayerTheme
import org.koin.android.ext.android.inject


class MainActivity : ComponentActivity() {

    private val mediaSessionAdapter: MediaController by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DefaultPreview()
        }
        startService(Intent(this, PlayerMediaService::class.java))
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Hey") },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = Color(0xFF0F2BCF)
                    )
                )
            },
            bottomBar = {
                BottomAppBar() {

                }
            }
        ) {

        }
    }
}


