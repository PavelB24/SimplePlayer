package com.barinov.simpleplayer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.barinov.simpleplayer.toSystemColorsContainer
import com.barinov.simpleplayer.ui.ColorsProvider
import com.barinov.simpleplayer.ui.Screen
import com.barinov.simpleplayer.ui.ScreenProvider
import com.barinov.simpleplayer.ui.components.WavesAnimatedHome

@Composable
fun HomeScreen(
    menuProvider: ScreenProvider,
    navController: NavHostController,
) {
    val isDark = isSystemInDarkTheme()
    val colors = ColorsProvider.obtainOnHomeScreen()
    LaunchedEffect(key1 = Unit) {
        if (menuProvider.currentScreen.value !is Screen.Home) {
            menuProvider.onScreenEnter(
                Screen.ScreenRegister.HOME,
                colors = colors.toSystemColorsContainer()
            )
        }
    }
    Box(
        modifier = Modifier
            .background(colors.uiGradient!!)
            .fillMaxSize()
    ) {
        WavesAnimatedHome {
            navController.navigate(Screen.ScreenRegister.SCAN.name)
        }
    }
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Image(
//            painter = painterResource(id = R.drawable.play_icon),
//            contentDescription = null,
//            modifier = Modifier
//                .clickable(
//                    remember { MutableInteractionSource() },
//                    selectableRipple()
//                ) {
//                    navController.navigate(Screen.ScreenRegister.IMPORT.name)
//                }
//                .size(animation.dp)
//        )
//    }
}
