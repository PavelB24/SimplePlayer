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


    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {

        val scaffoldState = rememberScaffoldState()
        val navState = rememberNavController()
        val provider = staticCompositionLocalOf<ScreenProvider> { error("NotProvided") }

        val currentScreen = remember {
            ScreenProvider()
        }
        CompositionLocalProvider(provider.provides(currentScreen)) {

            Scaffold(
                scaffoldState = scaffoldState,
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    currentScreen.currentScreen.value.Toolbar
//                    TopAppBar(
//                        navigationIcon = {},
//                        modifier = Modifier.height(58.dp),
//                        title = { },
//                        backgroundColor = primaryColor,
//                        actions = {
//                            appBarComponentFactory.GetMenuInstance(provider.current, navState)
//                        }
//
//                    )
                },
                backgroundColor = Color(0xDDE6E0E2),
                content = {
                    NavigationHost(
                        navState,
                        Pair(it.calculateBottomPadding(), it.calculateTopPadding()),
                        provider.current
                    )
                },
                bottomBar = {
                    AnimatedVisibility(
                        visible = currentScreen.currentScreen.value is Screen.CurrentPlay || currentScreen.currentScreen.value is Screen.Playlists,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        BottomAppBar(
//                            modifier = Modifier.nestedScroll(),
                            backgroundColor = primaryColor
                        ) {

                        }
                    }

                }
            )
        }
    }
}



