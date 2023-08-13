package com.barinov.simpleplayer.ui.components.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.barinov.simpleplayer.ui.theme.pb_color
import com.barinov.simpleplayer.ui.uiModels.SelectableSearchedItem

@Composable
fun SimpleScannedItem(
    item: SelectableSearchedItem,
    onClicked: (SelectableSearchedItem, Boolean) -> Unit
){
    Card(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp).clickable { onClicked.invoke(item, !item.checked) }.fillMaxWidth().padding(horizontal = 8.dp),
        elevation = 20.dp,
        shape = RoundedCornerShape(6.dp),
    ){
        Row(){
            Text(
                text = item.name,
                modifier = Modifier.weight(0.9f).padding(start = 8.dp)
            )
            Checkbox(
                checked = item.checked,
                onCheckedChange = {onClicked.invoke(item, it)},
                modifier = Modifier.weight(0.1f),
                colors = CheckboxDefaults.colors(
                    checkmarkColor = Color.White,
                    checkedColor = pb_color,
                    uncheckedColor = Color.Gray
                )
            )
        }
    }
}