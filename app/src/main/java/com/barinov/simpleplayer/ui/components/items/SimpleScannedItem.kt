package com.barinov.simpleplayer.ui.components.items

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.barinov.simpleplayer.domain.model.CommonFileItem
import com.barinov.simpleplayer.ui.uiModels.SelectableSearchedItem

@Composable
fun SimpleScannedItem(
    item: SelectableSearchedItem,
    onClicked: (SelectableSearchedItem, Boolean) -> Unit
){
    Card(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        elevation = 20.dp,
        shape = RoundedCornerShape(6.dp)
    ){
        Row(){
            Text(text = item.name)
            Checkbox(checked = item.checked, onCheckedChange = {onClicked.invoke(item, it)})
        }
    }
}