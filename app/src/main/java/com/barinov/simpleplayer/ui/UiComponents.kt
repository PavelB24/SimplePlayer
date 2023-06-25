package com.barinov.simpleplayer.ui

import android.graphics.drawable.VectorDrawable
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*


import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties

@Composable
fun AlertdialogComponent(
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
    icon: @Composable (() -> Unit)? = null, //if null then ignore above
    msg: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    confirmButton: @Composable () -> Unit,
    properties: DialogProperties? //if null then ignore above
) {
    val openDialog by remember { mutableStateOf(false) }
    if (!openDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = title,
            text = msg,
            dismissButton = dismissButton,
            confirmButton = confirmButton,
        )
    }
}

@Composable
fun AlertDialogMainBlock(
    messageRef: Int,
    checkedOnStart: Boolean,
    onCheckChanged: (Boolean) -> Unit
){
    Column() {
        Text(
            text = stringResource(id = messageRef),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 22.sp
        )
        Spacer(modifier = Modifier.height(18.dp))
        Checkbox(checked = checkedOnStart, onCheckedChange = {
            onCheckChanged.invoke(it)
        })
    }
}

//@Composable
//fun AlertDialogButtonsBlock(
//    onClick: DialogButtonActionsWithCheckBox<Boolean>,
//    checkState: Boolean
//){
//    Row() {
//        ElevatedButton(onClick = {
//            onClick.onPositiveButtonClicked(checkState)
//        }) {
//            Text(text = stringResource(id = android.R.string.cancel))
//        }
//
//        Spacer(modifier = Modifier.width(26.dp))
//
//        ElevatedButton(onClick = {
//            onClick.onPositiveButtonClicked(checkState)
//        }) {
//            Text(text = stringResource(id = android.R.string.ok))
//        }
//    }
//}



@Composable
fun MenuImageButton(
    image: ImageVector,
    onClick: () -> Unit
){
    IconButton(onClick = { onClick.invoke()}) {
        Icon(image, "")
    }
}

@Composable
fun ExpandedMenu(
    refs: Array<Int>, //strings
    onClick: (Int) -> Unit
) {
    Log.d("@@@", "MENU")
    var expanded by remember { mutableStateOf(false) }
    Box {
        AnimatedVisibility(visible = refs.isNotEmpty()) {
            IconButton(onClick = {
                expanded = !expanded
            }
            ) {
                Icon(Icons.Default.MoreVert, "", tint = Color(0xDDDDDDDD))
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


