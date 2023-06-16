package com.barinov.simpleplayer.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.barinov.simpleplayer.ui.theme.SimplePlayerTheme

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DefaultPreview()
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview(){
    Scaffold(
        topBar = {
            TopAppBar (
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


