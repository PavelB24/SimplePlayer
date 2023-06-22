package com.barinov.simpleplayer.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.barinov.simpleplayer.base.DialogButtonActions
import org.koin.androidx.compose.inject

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

@Composable
fun AlertDialogButtonsBlock(){
    Row() {
        ElevatedButton(onClick = { /*TODO*/ }) {
            
        }
        Spacer(modifier = Modifier.width(26.dp))
        ElevatedButton(onClick = { /*TODO*/ }) {

        }
    }
}


@Composable
fun DropDownList(
    requestToOpen: Boolean = false,
    refs: Array<Int>,
    request: (Boolean) -> Unit,
    selectedId: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(requestToOpen) }
    Box {
        TextButton(onClick = { expanded = !expanded }) {
            Text("DropDown")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            refs.forEach { label ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        selectedId(label)
                    },
                    text = { Text(stringResource(id = label)) }
                )
            }
        }
    }
}


