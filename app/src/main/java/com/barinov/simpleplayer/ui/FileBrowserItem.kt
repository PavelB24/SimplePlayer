package com.barinov.simpleplayer.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.barinov.simpleplayer.base.ItemInteractor
import com.barinov.simpleplayer.domain.model.CommonFileItem
import com.barinov.simpleplayer.getName
import com.barinov.simpleplayer.getSize
import com.barinov.simpleplayer.isFile
import com.barinov.simpleplayer.sizeBytesToMb

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileItem(item: CommonFileItem, interactor: ItemInteractor<CommonFileItem>) {
    val isFile = item.isFile()
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth()
            .combinedClickable(
                remember { MutableInteractionSource() },
                selectableRipple(40.dp),
                onClick = {
                    interactor.onClick(item)
                },
                onLongClick = {
                    interactor.onLongClick(item)
                }
            ),
        elevation = 20.dp,
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FileEntityImage(isFile)
            Text(
                text = item.getName(),
                maxLines = 1,
                style = TextStyle(
                    textAlign = TextAlign.Start
                ),//here is style
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(start = if (!isFile) 26.dp else 10.dp)
                    .weight(0.8f)
            )
            Spacer(modifier = Modifier.width(20.dp))
            if (isFile) {
                Text(
//                    text = item.getSize().sizeBytesToMb(),
                    text = item.getSize().sizeBytesToMb(),
                    style = TextStyle(
                        textAlign = TextAlign.End
                    ),//here is style
                    fontSize = 16.sp,
                    modifier = Modifier.weight(0.2f)
                )
            }

        }
    }
}

@Composable
fun FileEntityImage(isFile: Boolean) {
    Image(
        painter = painterResource(
            id = (if (isFile) com.barinov.simpleplayer.R.drawable.file
            else com.barinov.simpleplayer.R.drawable.folder)
        ),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.padding(20.dp, top = 5.dp, bottom = 5.dp)
    )
}