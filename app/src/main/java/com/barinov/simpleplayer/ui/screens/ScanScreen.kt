package com.barinov.simpleplayer.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.barinov.simpleplayer.R
import com.barinov.simpleplayer.domain.FileWorker
import com.barinov.simpleplayer.domain.model.CommonFileItem
import com.barinov.simpleplayer.ellipsizePath
import com.barinov.simpleplayer.extractPath
import com.barinov.simpleplayer.ui.Screen
import com.barinov.simpleplayer.ui.theme.path_card_color
import com.barinov.simpleplayer.ui.theme.pb_color
import com.barinov.simpleplayer.ui.viewModels.ScanViewModel
import org.koin.androidx.compose.getViewModel


const val PATH_KEY = "path"

@Composable
fun ScanScreen(
    navController: NavHostController
) {

    val viewModel: ScanViewModel = getViewModel()

    val events = viewModel.events.collectAsState(initial = FileWorker.FileWorkEvents.Idle)


    val sizeToCopy = remember {
        mutableStateOf(0)
    }

//    val totalCopy = remember {
//        mutableStateOf(0f)
//    }


    val playlistName = rememberSaveable { mutableStateOf("") }
    val searchPath = remember {
        mutableStateOf(
            extractFolder(navController, viewModel.getDefaultRoot())
        )
    }
    val copyCb = rememberSaveable { mutableStateOf(false) }


    when (events.value) {
        is FileWorker.FileWorkEvents.OnCopyStarted -> {
            sizeToCopy.value =
                (events.value as FileWorker.FileWorkEvents.OnCopyStarted).megaBytesToCopy ?: 0
        }

//        is FileWorker.FileWorkEvents.OnBlockCopied -> {
//            totalCopy.value += (events.value as FileWorker.FileWorkEvents.OnBlockCopied).megaBytes
//        }

        is FileWorker.FileWorkEvents.OnCompleted -> {

        }

        else -> {}
    }

    Box(
        modifier = Modifier
//                .background(Color.White)
//            .widthPaddingByDisplayMetrics(LocalContext.current, 15)
            .animateContentSize(),
//            elevation = 8.dp
//            contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "Scan for music",
                    maxLines = 1,
                    fontSize = 22.sp,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                label = { Text(text = "PlaylistName") },
                singleLine = true,
                value = playlistName.value,
                enabled = events.value is FileWorker.FileWorkEvents.Idle,
                onValueChange = {
                    playlistName.value = it
                })

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Checkbox(
                    checked = copyCb.value,
                    onCheckedChange = { copyCb.value = it },
                    colors = CheckboxDefaults.colors(
                        checkmarkColor = Color.White,
                        checkedColor = pb_color,
                        uncheckedColor = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.width(22.dp))
                Text(
                    text = stringResource(id = R.string.copy_on_scan_title),
                    maxLines = 2,
                    fontSize = 16.sp,
                    modifier = Modifier.clickable {
                        copyCb.value = !copyCb.value
                    }
                )
            }


            Spacer(modifier = Modifier.height(16.dp))
            //
            Card(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                elevation = 0.dp,
                backgroundColor = path_card_color
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(start = 16.dp),
//                            enabled = events.value is FileWorker.FileWorkEvents.Idle,
                        text = searchPath.value.extractPath().ellipsizePath(12),
                        maxLines = 2,
                        fontSize = 22.sp,
                    )
                    Image(
                        painter = painterResource(id = R.drawable.folder),
                        contentScale = ContentScale.Crop,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable {
                                if (events.value is FileWorker.FileWorkEvents.Idle) {
                                    navController.navigate(Screen.ScreenRegister.IMPORT.name)
                                } else {
                                    viewModel.skipState()
                                    navController.navigate(Screen.ScreenRegister.IMPORT.name)
                                }

                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            AnimatedVisibility(visible = events.value is FileWorker.FileWorkEvents.OnBlockCopied || events.value is FileWorker.FileWorkEvents.OnCopyStarted) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
//                        if (events.value is FileWorker.FileWorkEvents.OnBlockCopied) {
//                            Log.d(
//                                "@@@",
//                                "${(events.value as FileWorker.FileWorkEvents.OnBlockCopied).megaBytes}.mb"
//                            )
//                        }
//                        val modifier =
//                            if (events.value is FileWorker.FileWorkEvents.OnBlockCopied) {
//                                Modifier.progressSemantics(
//                                    value = (events.value as FileWorker.FileWorkEvents.OnBlockCopied).megaBytes,
//                                    valueRange = 0f..sizeToCopy.value
//                                )
//                            } else {
//                                Modifier
//                            }
//                        LinearProgressIndicator(modifier)
                    if (events.value is FileWorker.FileWorkEvents.OnBlockCopied) {
                        LinearProgressIndicator(
                            ((events.value as FileWorker.FileWorkEvents.OnBlockCopied).megaBytes / sizeToCopy.value.toFloat()),
                            color = pb_color,
                            modifier = Modifier.weight(0.6f)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    if (events.value is FileWorker.FileWorkEvents.OnBlockCopied) {
                        Text(
                            text = "${(events.value as FileWorker.FileWorkEvents.OnBlockCopied).megaBytes}mb/${sizeToCopy.value}mb",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(0.4f)
                        )
                    }
                }
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                fontSize = 20.sp,
                textAlign = TextAlign.Start,
                text = when (events.value) {
                    is FileWorker.FileWorkEvents.OnSearchCompleted -> {
                        stringResource(
                            id = R.string.on_tracks_found,
                            (events.value as FileWorker.FileWorkEvents.OnSearchCompleted).count
                        )
                    }

                    is FileWorker.FileWorkEvents.NoMusicFound -> {
                        stringResource(id = R.string.on_no_tracks_found)
                    }

                    else -> {
                        ""
                    }
                }
            )

//                }


            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
//                Button(
//                    onClick = { dialogExtender.value = false },
//                    enabled = getButtonAccessByState(events.value)
//                ) {
//                    Text(text = stringResource(id = android.R.string.cancel))
//                }
                Spacer(modifier = Modifier.width(26.dp))

                Button(
                    onClick = {
                        when (events.value) {
                            is FileWorker.FileWorkEvents.Error -> {}
                            FileWorker.FileWorkEvents.Idle -> {
                                viewModel.startScan(
                                    playlistName.value,
                                    searchPath.value
                                )
                            }

                            is FileWorker.FileWorkEvents.OnSearchCompleted -> {
                                viewModel.confirm(copyCb.value, playlistName.value)
                            }

                            else -> {}
                        }
                    },
                    enabled = getButtonAccessByState(events.value)
                ) {
                    if (events.value is FileWorker.FileWorkEvents.Idle) {
                        Text(text = stringResource(id = R.string.start_scan))
                    } else {
                        Text(text = stringResource(id = android.R.string.ok))
                    }
                }

            }


        }
    }
}

private fun getButtonAccessByState(state: FileWorker.FileWorkEvents): Boolean =
    state is FileWorker.FileWorkEvents.Idle
            || state is FileWorker.FileWorkEvents.OnSearchCompleted
            || state is FileWorker.FileWorkEvents.NoMusicFound


private fun extractFolder(
    navController: NavHostController,
    default: CommonFileItem
): CommonFileItem {
    return navController.currentBackStackEntry?.savedStateHandle?.get<Array<CommonFileItem>>(
        PATH_KEY
    )?.first() ?: default
}