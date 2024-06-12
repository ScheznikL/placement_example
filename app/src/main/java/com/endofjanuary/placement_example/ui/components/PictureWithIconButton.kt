package com.endofjanuary.placement_example.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun PictureWithIconButton(
    modifier: Modifier = Modifier,
    picture: @Composable (() -> Unit),
    icon: @Composable (() -> Unit),
    onClick: () -> Unit
) {
    Box(contentAlignment = Alignment.TopEnd, modifier = modifier) {
        picture()
        IconButton(
            onClick = onClick,
            modifier = Modifier.padding(end = 15.dp)
        ) {
            icon()
        }
    }
}