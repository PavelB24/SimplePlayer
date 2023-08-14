package com.barinov.simpleplayer.ui.components

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FloatTweenSpec
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center


import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.barinov.simpleplayer.R
import com.barinov.simpleplayer.domain.RootType
import com.barinov.simpleplayer.ui.theme.selectable_color
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch





@Composable
fun TrackPlayInfo(){
    //ControlButtons
//    Row() {
//
//    }
//
//    Row {
//        LinearProgressIndicator(progress = )
//    }
}

@Composable
inline fun WavesAnimatedHome(
   crossinline onClick: suspend () -> Unit = {}
) {
    

    val coroutine = rememberCoroutineScope()
    val waves = listOf(
        remember { Animatable(0f) },
        remember { Animatable(0f) },
        remember { Animatable(0f) },
        remember { Animatable(0f) },
    )

    val clickAnim = remember { Animatable(250f) }


    val animationSpec = infiniteRepeatable<Float>(
        animation = tween(4000, easing = FastOutLinearInEasing),
        repeatMode = RepeatMode.Restart,
    )
    waves.forEachIndexed { index, animatable ->
        LaunchedEffect(animatable) {
            delay(index * 1000L)
            animatable.animateTo(
                targetValue = 1f, animationSpec = animationSpec
            )
        }
    }


    val dys = waves.map { it.value }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Center
    ) {
        // Waves
        dys.forEach { dy ->
            Box(
                Modifier
                    .size(clickAnim.value.dp)
                    .align(Center)
                    .graphicsLayer {
                        scaleX = dy * 4 + 1
                        scaleY = dy * 4 + 1
                        alpha = 1 - dy
                    },
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(color = Color(0xDD8FF39C), shape = CircleShape)
                )
            }
        }

        Image(
            painter = painterResource(id = R.drawable.play_icon),
            contentDescription = null,
            modifier = Modifier
                .clickable(
                    remember { MutableInteractionSource() },
                    selectableRipple(50.dp)
                ) {
                    coroutine.launch {
                        clickAnim.animateTo(
                            300f, animationSpec = FloatTweenSpec(
                                1000
                            )
                        )
                        if (clickAnim.value == 300f) {
                            clickAnim.animateTo(
                                250f, animationSpec = FloatTweenSpec(
                                    500
                                )
                            )
                            onClick()
                        }
                    }

                }
                .size(clickAnim.value.dp)
        )
    }

}


@Composable
fun TopBarBackButton(
    onPressed: () -> Unit
){
    IconButton(
        onClick = { onPressed() },
        Modifier.padding(start = 12.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_arrow_back),
            contentDescription = "back",
            tint = Color.White,
            modifier = Modifier.size(40.dp)
        )
    }
}

@Composable
fun RotatingRootButton(
    startValue: RootType,
    rt: StateFlow<RootType>,
    onClick: () -> Unit

) {
    val coroutine = rememberCoroutineScope()
    var xRotation by remember {
        mutableStateOf(0f)
    }

    val rtState = rt.collectAsState()

    IconButton(
        onClick = {
            coroutine.launch {
                animate(
                    0f,
                    360f,
                    animationSpec = tween(800, easing = LinearOutSlowInEasing),
                    block = { value, _ ->
                        xRotation = value
                        if (value in 90f .. 95f) {
                            Log.d("@@@", "Inv")
                            onClick.invoke()
                        }

                    }
                )
            }
        },
        Modifier
            .size(50.dp)

    ) {
        Icon(
            painter = painterResource(
                id = if ((rtState.value) == RootType.INTERNAL) {
                    R.drawable.phone
                } else {
                    R.drawable.usb_logo
                }
            ),
            "",
            tint = Color.Unspecified,
            modifier = Modifier.graphicsLayer {
                rotationY = xRotation
            }
        )
    }
}

@Composable
fun AddFromCurrentDirImageButton(
    onClick: () -> Unit
) {
//    DisposableEffect(key1 = , effect = )
//    val infiniteTransition = rememberInfiniteTransition()
//    val animation by infiniteTransition.animateFloat(
//        initialValue = 60.0f,
//        targetValue = 70.0f,
//        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse)
//    )
    IconButton(
        onClick = { onClick.invoke() },
        Modifier.size(50.dp).padding(end = 12.dp)
    ) {
        Icon(painter = painterResource(
            id = R.drawable.add_tracks),
            "",
            tint = Color.White
        )
//        ExpandedMenu(arrayOf(), {})
    }
}

@Composable
fun selectableRipple(radius: Dp) = rememberRipple(color = selectable_color, radius = radius)


@Composable
fun ExpandedMenu(
    refs: Array<Int>, //strings
    onClick: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        AnimatedVisibility(visible = refs.isNotEmpty()) {
            IconButton(onClick = {
                expanded = !expanded
            }
            ) {
                AnimatedExpandIcon()
//                Icon(Icons.Default.MoreVert, "", tint = Color(0xDDDDDDDD))
//            Text("DropDown")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
//            modifier = Modifier.fillMaxWidth()
            ) {
                refs.forEach { label ->
                    DropdownMenuItem(
                        onClick = {
                            expanded = false
                            onClick(label)
                        }) {
                        Text(stringResource(id = label))
                    }
                }
            }
        }
    }
}


@Composable
fun AnimatedExpandButton(
    onClick: () -> Unit
) {

    Icon(Icons.Default.MoreVert, "", tint = Color.White)
}


@Composable
fun AnimatedExpandIcon() {

    val dots = arrayOf(
        remember { Animatable(0f) },
        remember { Animatable(0f) },
        remember { Animatable(0f) },
    )

    val dropDownExp = remember {
        mutableStateOf(false)
    }

//    val animationSpec = infiniteRepeatable<Float>(
//        animation = tween(4000, easing = FastOutLinearInEasing),
//        repeatMode = RepeatMode.Restart,
//    )

    dots.forEachIndexed { index, animatable ->
        LaunchedEffect(animatable) {
            delay(index * 150L)
            animatable.animateTo(
                targetValue = 1f, animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 3000
                        0.0f at 0 with LinearOutSlowInEasing // for 0-15 ms
                        0.5f at 300 with LinearOutSlowInEasing // for 15-75 ms
                        0.2f at 800 with LinearOutSlowInEasing // for 0-15 ms
                        0.0f at 3000
                    },
                    repeatMode = RepeatMode.Restart,
                )
            )
        }
    }

    val dys = dots.map { it.value }

    val travelDistance = with(LocalDensity.current) { 10.dp.toPx() }

    Column(
//        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Arrangement.h
        modifier = Modifier.clickable {
            dropDownExp.value = !dropDownExp.value
        }
    ) {
        dys.forEachIndexed { index, dy ->
            Box(
                Modifier
                    .size(4.dp)
                    .graphicsLayer {
                        translationY = -dy * travelDistance
                    },
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(color = Color.White, shape = CircleShape)
                )
            }

            if (index != dys.size - 1) {
                Spacer(modifier = Modifier.height(7.dp))
            }
        }
    }
}


