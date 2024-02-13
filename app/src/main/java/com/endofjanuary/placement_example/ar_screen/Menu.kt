package com.endofjanuary.placement_example.ar_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.endofjanuary.placement_example.ModelObject
import com.endofjanuary.placement_example.R

@Composable
fun Menu(modifier: Modifier, onClick: (String) -> Unit) {
    var currentIndex by remember {
        mutableStateOf(0)
    }
    val itemsList = listOf(
        ModelObject("model_v2_chair", R.drawable.preview_model),
        ModelObject("model_v2_refine", R.drawable.refine_model),
        ModelObject("damaged_helmet", R.drawable.ic_launcher_foreground),
    )

    fun updateIndex(offset: Int) {
        currentIndex = (currentIndex + offset + itemsList.size) % itemsList.size
        onClick(itemsList[currentIndex].name)
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        IconButton(onClick = { updateIndex(-1) }) {
            Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "previous")
        }
        CircularImage(thumbnailPath = itemsList[currentIndex].thumbnailId)
        IconButton(onClick = { updateIndex(1) }) {
            Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "next")
        }
    }
}

@Composable
fun CircularImage(
    thumbnailPath: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(140.dp)
            .clip(CircleShape)
            .border(width = 2.dp, color = Color.Cyan),
    ) {
        Image(
            painter = painterResource(id = thumbnailPath),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
    }
}