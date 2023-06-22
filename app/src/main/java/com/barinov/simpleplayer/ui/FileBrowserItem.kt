package com.barinov.simpleplayer.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
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
fun FileItem(item: CommonFileItem, interactor: ItemInteractor<CommonFileItem>){
    val isFile = item.isFile()
    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RectangleShape
    ) {
        Row(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        interactor.onClick(item)
                    },
                    onLongClick = {
                        interactor.onLongClick(item)
                    }
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FileEntityItem(isFile)
            Text(text = item.getName(),
                style = TextStyle(
                    textAlign = TextAlign.Center
                ),//here is style
                fontSize = 28.sp,
                modifier =  Modifier.padding(start = 20.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            AnimatedVisibility(visible = isFile) {
                Text(text = item.getSize().sizeBytesToMb(),
                    style = TextStyle(
                        textAlign = TextAlign.Center
                    ),//here is style
                    fontSize = 28.sp,
                    modifier =  Modifier
                )
            }

        }
    }
}

@Composable
fun FileEntityItem(isFile: Boolean){
    Image(painter = painterResource(
        id = (if (isFile) com.barinov.simpleplayer.R.drawable.file
        else com.barinov.simpleplayer.R.drawable.folder)),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.padding(10.dp)
//            .height().width()
    )
}