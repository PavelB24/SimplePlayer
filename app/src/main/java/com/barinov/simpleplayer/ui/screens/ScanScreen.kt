package com.barinov.simpleplayer.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.barinov.simpleplayer.R
import com.barinov.simpleplayer.domain.FileWorker
import com.barinov.simpleplayer.domain.model.CommonFileItem
import com.barinov.simpleplayer.ellipsizePath
import com.barinov.simpleplayer.extractPath
import com.barinov.simpleplayer.toSystemColorsContainer
import com.barinov.simpleplayer.ui.ColorsContainer
import com.barinov.simpleplayer.ui.ColorsProvider
import com.barinov.simpleplayer.ui.Screen
import com.barinov.simpleplayer.ui.ScreenProvider
import com.barinov.simpleplayer.ui.components.TopBarBackButton
import com.barinov.simpleplayer.ui.components.items.SimpleScannedItem
import com.barinov.simpleplayer.ui.theme.action_color
import com.barinov.simpleplayer.ui.theme.path_card_color
import com.barinov.simpleplayer.ui.theme.pb_color
import com.barinov.simpleplayer.ui.uiModels.SelectableSearchedItem
import com.barinov.simpleplayer.ui.viewModels.ScanViewModel
import org.koin.androidx.compose.getViewModel


const val PATH_KEY = "path"

@Composable
fun ScanScreen(
    navController: NavHostController,
    menuProvider: ScreenProvider,
) {

    val viewModel: ScanViewModel = getViewModel()
    val isSystemInDarkTheme = isSystemInDarkTheme()

    val colors =
        if (!isSystemInDarkTheme) ColorsProvider.obtainOnScanScreen() else ColorsProvider.obtainDefaultLight()

    val copyCb = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        onScreenEnter(navController, menuProvider, colors)
    }

    val sizeToCopy = remember {
        mutableStateOf(0)
    }

    val playlistName = rememberSaveable { mutableStateOf("") }
    val searchPath = remember {
        mutableStateOf(
            extractFolder(navController, viewModel.getDefaultRoot())
        )
    }

    val events =
        viewModel.events.collectAsState(initial = FileWorker.FileWorkEvents.Idle).also { state ->
            when (state.value) {
                is FileWorker.FileWorkEvents.OnCopyStarted -> {
                    sizeToCopy.value =
                        (state.value as FileWorker.FileWorkEvents.OnCopyStarted).megaBytesToCopy
                            ?: 0
                }

//        is FileWorker.FileWorkEvents.OnBlockCopied -> {
//            totalCopy.value += (events.value as FileWorker.FileWorkEvents.OnBlockCopied).megaBytes
//        }

                is FileWorker.FileWorkEvents.OnCompleted -> {

                }

                else -> {}
            }
        }



    Box(
        modifier = Modifier
            .background(Color.White) //NPE
            .animateContentSize()
            .fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 8.dp),
//                horizontalArrangement = Arrangement.Center
//            ) {
//
//                Text(
//                    text = "Scan for music",
//                    maxLines = 1,
//                    fontSize = 22.sp,
//                )
//            }
//            Spacer(modifier = Modifier.height(16.dp))

            PlayListNameField(playlistName, events)

            Spacer(modifier = Modifier.height(16.dp))

            PickPathComponent(searchPath, events, navController, viewModel)

            Spacer(modifier = Modifier.height(16.dp))

            ScanInfo()

            TracksTitle(events)

            SearchedTracksList(events)

            Spacer(modifier = Modifier.height(16.dp))

            CopyToInternalCheckBox(events, copyCb)

            Spacer(modifier = Modifier.height(26.dp))

            CopyProgressComponent(events, sizeToCopy)

            ScanScreenButtonsBlock(events, copyCb, viewModel, playlistName, searchPath)

        }
    }
}

@Composable
private fun ScanScreenButtonsBlock(
    events: State<FileWorker.FileWorkEvents>,
    copyCb: MutableState<Boolean>,
    viewModel: ScanViewModel,
    playlistName: MutableState<String>,
    searchPath: MutableState<CommonFileItem>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 16.dp),
        horizontalArrangement = Arrangement.End
    ) {

        Spacer(modifier = Modifier.width(26.dp))

        Button(
            colors = ButtonDefaults.buttonColors(backgroundColor = action_color),
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
            Text(
                modifier = Modifier.padding(5.dp),
                color = Color.White,
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 20.sp
                ),
                text = if (events.value is FileWorker.FileWorkEvents.Idle)
                    stringResource(id = R.string.start_scan) else
                    stringResource(id = android.R.string.ok)
            )
        }
    }

}

@Composable
private fun CopyProgressComponent(
    events: State<FileWorker.FileWorkEvents>,
    sizeToCopy: MutableState<Int>
) {
    AnimatedVisibility(visible = events.value is FileWorker.FileWorkEvents.OnBlockCopied || events.value is FileWorker.FileWorkEvents.OnCopyStarted) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
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

}

@Composable
private fun CopyToInternalCheckBox(
    events: State<FileWorker.FileWorkEvents>,
    copyCb: MutableState<Boolean>
) {
    AnimatedVisibility(visible = events.value is FileWorker.FileWorkEvents.OnSearchCompleted) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
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
    }
}

@Composable
private fun SearchedTracksList(events: State<FileWorker.FileWorkEvents>) {
    AnimatedVisibility(visible = events.value is FileWorker.FileWorkEvents.OnSearchCompleted) {
        Spacer(modifier = Modifier.height(16.dp))
        if (events.value is FileWorker.FileWorkEvents.OnSearchCompleted) {
            val lazyListState: LazyListState = rememberLazyListState()
            val searched = remember {
                mutableStateOf((events.value as? FileWorker.FileWorkEvents.OnSearchCompleted)?.names?.map {
                    SelectableSearchedItem(
                        it,
                        true
                    )
                })
            }
            searched.value?.let { searchedList ->
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 240.dp)
                ) {
                    items(
                        items = searchedList,
                        key = { it.hashCode() },
                        itemContent = {
                            SimpleScannedItem(item = it, onClicked = { item, checked ->
                                val index = searchedList.indexOf(item)
                                searched.value =
                                    searchedList.toMutableStateList().also { list ->
                                        list[index] = list[index].copy(checked = checked)
                                    }
                            })
                        }
                    )
                }
            }
        }

    }
}

@Composable
private fun TracksTitle(events: State<FileWorker.FileWorkEvents>) {
    AnimatedVisibility(visible = events.value is FileWorker.FileWorkEvents.OnSearchCompleted || events.value is FileWorker.FileWorkEvents.NoMusicFound) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            text = when (events.value) {
                is FileWorker.FileWorkEvents.OnSearchCompleted -> {
                    stringResource(
                        id = R.string.on_tracks_found,
                        (events.value as FileWorker.FileWorkEvents.OnSearchCompleted).names.size
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
    }
}

@Composable
private fun PickPathComponent(
    searchPath: MutableState<CommonFileItem>,
    events: State<FileWorker.FileWorkEvents>,
    navController: NavController,
    viewModel: ScanViewModel
) {
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
}

@Composable
private fun ScanInfo() {
//    Text(
//        modifier = Modifier.fillMaxWidth(),
//        text = ""
//    )
}

@Composable
private fun PlayListNameField(
    playlistName: MutableState<String>,
    events: State<FileWorker.FileWorkEvents>
) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        label = { Text(text = "PlaylistName") },
        singleLine = true,
        value = playlistName.value,
        enabled = events.value is FileWorker.FileWorkEvents.Idle,
        onValueChange = {
            playlistName.value = it
        })
}

private fun onScreenEnter(
    navController: NavHostController,
    menuProvider: ScreenProvider,
    colors: ColorsContainer
) {
    menuProvider.onScreenEnter(
        NavIcon = {
            TopBarBackButton {
                navController.navigateUp()
            }
        },
        screen = Screen.ScreenRegister.SCAN,
        colors = colors.toSystemColorsContainer()
    )
}

private fun getButtonAccessByState(state: FileWorker.FileWorkEvents): Boolean =
    state is FileWorker.FileWorkEvents.Idle
            || state is FileWorker.FileWorkEvents.OnSearchCompleted
            || state is FileWorker.FileWorkEvents.NoMusicFound


private fun extractFolder(
    navController: NavHostController,
    default: CommonFileItem
): CommonFileItem {
    val res = navController.currentBackStackEntry?.savedStateHandle?.get<Array<CommonFileItem>>(
        PATH_KEY
    )?.first() ?: default
    navController.currentBackStackEntry?.savedStateHandle?.set(PATH_KEY, null)
    return res
}