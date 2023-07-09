package com.barinov.simpleplayer.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.progressSemantics
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.barinov.simpleplayer.R
import com.barinov.simpleplayer.domain.FileWorker
import com.barinov.simpleplayer.domain.RootType
import com.barinov.simpleplayer.domain.model.CommonFileItem
import com.barinov.simpleplayer.ellipsizePath
import com.barinov.simpleplayer.extractPath
import com.barinov.simpleplayer.ui.Screen
import com.barinov.simpleplayer.ui.path_card_color
import com.barinov.simpleplayer.ui.viewModel.ScanViewModel
import org.koin.androidx.compose.getViewModel


const val PATH_KEY = "path"

@Composable
fun ScanDialog(
    navController: NavHostController,
    dialogExtender: MutableState<Boolean>
) {

    val viewModel: ScanViewModel = getViewModel()

    val events = viewModel.events.collectAsState(initial = FileWorker.FileEvents.Idle)

    val sizeToCopy = remember {
        mutableStateOf(0f)
    }
    val playlistName = rememberSaveable { mutableStateOf("") }
    val searchPath = remember {
        mutableStateOf(
            extractFolder(navController, viewModel.getDefaultRoot())
        )
    }
    val copyCb = rememberSaveable { mutableStateOf(false) }

    if (events.value is FileWorker.FileEvents.OnCopyStarted) {
        sizeToCopy.value = (events.value as FileWorker.FileEvents.OnCopyStarted).megaBytesToCopy
    }


    Dialog(
        onDismissRequest = { dialogExtender.value = false },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {

        Box(
            modifier = Modifier.background(Color.White),
            contentAlignment = Alignment.Center
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
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
                    onValueChange = {
                        playlistName.value = it
                    })

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Checkbox(checked = copyCb.value, onCheckedChange = { copyCb.value = it })
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
                                    navController.navigate(Screen.ScreenRegister.IMPORT.name)

                                }
                        )
                    }
                }

                AnimatedVisibility(visible = events.value is FileWorker.FileEvents.OnBlockCopied || events.value is FileWorker.FileEvents.OnCopyStarted) {
                    Row() {
                        val modifier = if (events.value is FileWorker.FileEvents.OnBlockCopied) {
                            Modifier.progressSemantics(
                                value = (events.value as FileWorker.FileEvents.OnBlockCopied).megaBytes,
                                valueRange = 0f..sizeToCopy.value
                            )
                        } else {
                            Modifier
                        }
                        LinearProgressIndicator(modifier)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = if (events.value is FileWorker.FileEvents.OnBlockCopied) "" else "")
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { dialogExtender.value = false },
                        enabled = events.value is FileWorker.FileEvents.Idle || events.value is FileWorker.FileEvents.OnSearchCompleted
                    ) {
                        Text(text = stringResource(id = android.R.string.cancel))
                    }
                    Spacer(modifier = Modifier.width(26.dp))

                    Button(
                        onClick = { /*TODO*/ },
                        enabled = events.value is FileWorker.FileEvents.Idle || events.value is FileWorker.FileEvents.OnSearchCompleted
                    ) {

                    }

                    //2 кнопки
                }


            }
        }
    }
}



private fun extractFolder(
    navController: NavHostController,
    default: CommonFileItem
): CommonFileItem{
   return navController.currentBackStackEntry?.savedStateHandle?.get<ArrayList<CommonFileItem>>(PATH_KEY)?.first() ?: default
}